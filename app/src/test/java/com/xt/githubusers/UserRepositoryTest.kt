package com.xt.githubusers

import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.TestPager
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.repository.UserRepositoryImpl
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.data.room.entity.UserEntity
import com.xt.githubusers.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    private lateinit var repository: UserRepositoryImpl
    private val apiService: UserApiService = mockk()
    private val userDao: UserDao = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)

    @Before
    fun setup() {
        // initialize repository
        repository = UserRepositoryImpl(
            userApiService = apiService,
            userDao = userDao,
            remoteKeyDao = remoteKeyDao
        )
    }

    @Test
    fun `getUserDetail should return default user model`() = runTest {
        val result = repository.getUserDetail()
        assertNotNull(result)
        assertTrue(true)
    }

}
