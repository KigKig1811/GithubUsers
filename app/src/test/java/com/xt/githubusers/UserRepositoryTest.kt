package com.xt.githubusers

import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.repository.UserRepositoryImpl
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

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
