package com.xt.githubusers

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.repository.UserRepository
import com.xt.githubusers.domain.usecase.FetchUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class FetchUsersUseCaseTest {

    private lateinit var fetchUsersUseCase: FetchUsersUseCase
    private val fakeUserRepository: UserRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fetchUsersUseCase = FetchUsersUseCase(fakeUserRepository)
    }

    @Test
    fun `invoke should return flow of paging data`() = runTest {
        // Arrange
        val fakeUsers = listOf(UserModel(1, "Alice"), UserModel(2, "Bob"))
        val fakePagingData = PagingData.from(fakeUsers)

        coEvery { fakeUserRepository.fetchUsers(any()) } returns flowOf(fakePagingData)

        // Act
        val result = fetchUsersUseCase.invoke().asSnapshot()

        // Assert
        assertEquals(2, result.size)
        assertEquals("Alice", result[0].login)
        assertEquals("Bob", result[1].login)
    }

    @Test
    fun `invoke should return empty list when API returns no users`() = runTest {
        // Arrange
        val fakePagingData = PagingData.empty<UserModel>()

        coEvery { fakeUserRepository.fetchUsers(any()) } returns flowOf(fakePagingData)

        // Act
        val result = fetchUsersUseCase.invoke().asSnapshot()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke should handle exception when repository throws error`() = runTest {
        // Arrange
        coEvery { fakeUserRepository.fetchUsers(any()) } throws RuntimeException("API Error")

        // Act
        val result = runCatching { fetchUsersUseCase().asSnapshot() }

        // Assert
        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should execute in IO dispatcher`() = runTest {
        // Arrange
        val fakePagingData = PagingData.from(emptyList<UserModel>())
        coEvery { fakeUserRepository.fetchUsers(any()) } returns flowOf(fakePagingData)

        // Act
        val dispatcher = newSingleThreadContext("IO thread")
        withContext(dispatcher) {
            fetchUsersUseCase.invoke().collect{}
        }

        // Assert
        coVerify { fakeUserRepository.fetchUsers(any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}
