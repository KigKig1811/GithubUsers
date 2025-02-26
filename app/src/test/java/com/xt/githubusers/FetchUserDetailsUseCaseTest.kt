package com.xt.githubusers

import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.repository.UserRepository
import com.xt.githubusers.domain.usecase.FetchUserDetailsUseCase
import com.xt.githubusers.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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

@OptIn(ExperimentalCoroutinesApi::class)
class FetchUserDetailsUseCaseTest {

    private lateinit var fetchUserDetailsUseCase: FetchUserDetailsUseCase
    private val repository: UserRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fetchUserDetailsUseCase = FetchUserDetailsUseCase(repository)
    }
    @Test
    fun `invoke should return user details when API call is successful`() = runTest {
        // Arrange
        val userName = "Alice"
        val fakeUser = UserModel(id = 1, login = userName, avatarUrl = "https://example.com/avatar.png")

        coEvery { repository.getUserDetail(userName) } returns Result.Success(fakeUser)

        // Act
        val result = fetchUserDetailsUseCase.invoke(userName)

        // Assert
        assertTrue(result.isSuccess())
        assertEquals(fakeUser, result.getOrNull())
    }

    @Test
    fun `invoke should return failure`() = runTest {
        // Arrange
        val userName = "UnknownUser"
        coEvery { repository.getUserDetail(userName) } returns Result.Error(
            message = "User not found",
            code = 404
        )

        // Act
        val result = fetchUserDetailsUseCase(userName)

        // Assert
        assertTrue(result.isError())
        assertNull(result.getOrNull())
    }

    @Test
    fun `getUserDetail should return failure when API returns empty response`() = runTest {
        // Arrange
        val userName = "EmptyUser"
        coEvery { repository.getUserDetail(userName) } returns Result.Success(UserModel())

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