package com.xt.githubusers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.xt.githubusers.data.datasource.UserRemoteMediator
import com.xt.githubusers.data.dto.UserDto
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.data.room.entity.RemoteKeyEntity
import com.xt.githubusers.data.room.entity.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
class UserRemoteMediatorTest {

    private lateinit var remoteMediator: UserRemoteMediator
    private val userDao: UserDao = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)
    private val apiService: UserApiService = mockk()

    @Before
    fun setup() {
        // Set the main dispatcher for testing coroutines
        Dispatchers.setMain(StandardTestDispatcher())
        remoteMediator = UserRemoteMediator(
            userDao = userDao,
            remoteKeyDao = remoteKeyDao,
            userApiService = apiService
        )
    }

    @Test
    fun `load - refresh success, data inserted, nextKey updated`() = runTest {
        // Fake API response
        val fakeUsers = listOf(UserDto(1, "John Doe"), UserDto(2, "Jane Doe"))
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(fakeUsers)

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call load with LoadType.REFRESH
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Verify the result is successful and more data is available
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        // Verify database operations were called correctly
        coVerify { userDao.clearUsers() }
        coVerify { remoteKeyDao.clearRemoteKeys() }
        coVerify { userDao.insertAll(any()) }
        coVerify {
            remoteKeyDao.insert(withArg { remoteKey ->
                assertEquals(0, remoteKey.id)
                assertEquals(1, remoteKey.nextKey)
            })
        }
    }

    @Test
    fun `load - append success, more data available`() = runTest {
        // Fake remote key (indicating there was previous data)
        coEvery { remoteKeyDao.remoteKey(0) } returns RemoteKeyEntity(id = 0, nextKey = 1)

        // Fake API response
        val fakeUsers = listOf(UserDto(3, "John Smith"))
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(fakeUsers)

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call load with LoadType.APPEND
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        // Verify the result is successful and more data is available
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        // Verify database operations were called correctly
        coVerify { userDao.insertAll(any()) }
        coVerify {
            remoteKeyDao.insert(withArg { remoteKey ->
                assertEquals(0, remoteKey.id)
                assertEquals(2, remoteKey.nextKey)
            })
        }
    }

    @Test
    fun `load - append end of pagination`() = runTest {
        // Fake remote key with nextKey = null (no more pages available)
        coEvery { remoteKeyDao.remoteKey(0) } returns RemoteKeyEntity(id = 0, nextKey = null)

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call load with LoadType.APPEND
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        // Verify the result indicates the end of pagination
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load - API error, return MediatorResult Error`() = runTest {
        // Fake an API error
        coEvery { apiService.fetchUsers(any(), any()) } throws Exception("Network Error")

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call load with LoadType.REFRESH
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Verify the result returns an error
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun `load - append with empty database, should return endOfPaginationReached`() = runTest {
        // Fake remote key
        coEvery { remoteKeyDao.remoteKey(0) } returns RemoteKeyEntity(id = 0, nextKey = null)

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call LoadType.APPEND
        val result = remoteMediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load - refresh with empty API response, should return endOfPaginationReached`() = runTest {
        // Fake API response
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(emptyList())

        // Fake paging state
        val pagingState = PagingState<Int, UserEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // Call LoadType.REFRESH
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load - database error when inserting users, should return MediatorResult Error`() =
        runTest {
            // Fake API response
            val fakeUsers = listOf(UserDto(1, "John Doe"), UserDto(2, "Jane Doe"))
            coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(fakeUsers)

            coEvery { userDao.insertAll(any()) } throws Exception("Database error")

            // Fake paging state
            val pagingState = PagingState<Int, UserEntity>(
                pages = emptyList(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0
            )

            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            assertTrue(result is RemoteMediator.MediatorResult.Error)
        }

    @After
    fun tearDown() {
        // Reset the dispatcher after tests
        Dispatchers.resetMain()
    }
}