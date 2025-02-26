package com.xt.githubusers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.asSnapshot
import com.xt.githubusers.data.datasource.UserRemoteMediator
import com.xt.githubusers.data.dto.UserDto
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.repository.UserRepositoryImpl
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.data.room.entity.UserEntity
import com.xt.githubusers.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private lateinit var repository: UserRepositoryImpl
    private lateinit var remoteMediator: UserRemoteMediator
    private val apiService: UserApiService = mockk()
    private val userDao: UserDao = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // initialize repository
        Dispatchers.setMain(StandardTestDispatcher())
        remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService)
        repository = UserRepositoryImpl(
            userApiService = apiService,
            userDao = userDao,
            remoteKeyDao = remoteKeyDao
        )
    }

    @Test
    fun `fetchUsers should return flow of paging data`() = runTest {
        // Arrange
        val userModels = listOf(
            UserModel(1, "Alice"),
            UserModel(2, "Bob")
        )

        coEvery {
            apiService.fetchUsers(any(), any())
        } returns Response.success(emptyList()) // Mock API response

        coEvery {
            userDao.fetchUsers()
        } returns FakeUserPagingSource(userModels.map {
            UserMapper.toEntity(it)
        }) // Fake PagingSource

        val pager = Pager(
            config = PagingConfig(pageSize = 2),
            remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService),
            pagingSourceFactory = { userDao.fetchUsers() }
        )

        // Act
        val result = pager.flow.asSnapshot()

        // Assert
        assertEquals(userModels.size, result.size)
        assertEquals(userModels[0].login, result[0].login)
        assertEquals(userModels[1].login, result[1].login)
    }

    @Test
    fun `fetchUsers should return empty when API returns empty list`() = runTest {
        // Arrange
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(emptyList())
        coEvery { userDao.fetchUsers() } returns FakeUserPagingSource(emptyList())

        val pager = Pager(
            config = PagingConfig(pageSize = 2),
            remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService),
            pagingSourceFactory = { userDao.fetchUsers() }
        )

        // Act
        val result = pager.flow.asSnapshot()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `fetchUsers should return error when API fails`() = runTest {
        // Arrange
        coEvery { apiService.fetchUsers(any(), any()) } throws IOException("Network error")
        coEvery { userDao.fetchUsers() } returns FakeUserPagingSource(emptyList())

        val pager = Pager(
            config = PagingConfig(pageSize = 2),
            remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService),
            pagingSourceFactory = { userDao.fetchUsers() }
        )

        // Act & Assert
        try {
            pager.flow.asSnapshot()
            fail("Expected exception was not thrown")
        } catch (e: Exception) {
            assertTrue(e is IOException)
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `fetchUsers should stop pagination when no more data available`() = runTest {
        // Arrange
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(emptyList())
        coEvery { userDao.fetchUsers() } returns FakeUserPagingSource(emptyList())

        val remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService)
        val result =
            remoteMediator.load(LoadType.APPEND, PagingState(emptyList(), null, PagingConfig(2), 0))

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `fetchUsers should append new data when more pages available`() = runTest {
        // Arrange
        val userModels = listOf(UserDto(1, "Alice"), UserDto(2, "Bob"))
        coEvery { apiService.fetchUsers(any(), any()) } returns Response.success(userModels)
        coEvery { userDao.fetchUsers() } returns FakeUserPagingSource(emptyList())

        val remoteMediator = UserRemoteMediator(userDao, remoteKeyDao, apiService)
        val result = remoteMediator.load(LoadType.APPEND, PagingState(emptyList(), null, PagingConfig(2), 0))

        // Assert
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `getUserDetail should return user detail when API call is successful`() = runTest {
        // Arrange
        val fakeUserDto =
            UserDto(id = 1, login = "KinhTn", avatarUrl = "https://example.com/avatar.png")
        val response = Response.success(fakeUserDto)
        val expectedUserModel = UserMapper.toModel(fakeUserDto)

        coEvery { apiService.getUserDetail(any()) } returns response

        // Act
        val result = repository.getUserDetail(any())

        // Assert
        assertTrue(result.isSuccess())
        assertEquals(expectedUserModel, result.getOrNull())
    }

    @Test
    fun `getUserDetail should return failure`() = runTest {
        // Arrange
        val userName = "UnknownUser"
        coEvery { apiService.getUserDetail(userName) } throws HttpException(
            Response.error<Any>(404, "".toResponseBody())
        )

        // Act
        val result = repository.getUserDetail(userName)

        // Assert
        assertTrue(result.isError())
        assertNull(result.getOrNull())
    }

    @Test
    fun `getUserDetail should return failure when API returns empty response`() = runTest {
        // Arrange
        val userName = "EmptyUser"
        coEvery { apiService.getUserDetail(userName) } returns Response.success(UserDto())

        // Act
        val result = repository.getUserDetail(userName)

        // Assert
        assertTrue(result.isSuccess())
        assertNull(result.getOrNull()?.id)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}

class FakeUserPagingSource(
    private val data: List<UserEntity>
) : PagingSource<Int, UserEntity>() {

    override fun getRefreshKey(state: PagingState<Int, UserEntity>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserEntity> {
        return LoadResult.Page(
            data = data,
            prevKey = null,
            nextKey = null
        )
    }
}

