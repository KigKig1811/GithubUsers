package com.xt.githubusers

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.usecase.FetchUsersUseCase
import com.xt.githubusers.presentation.users.UsersViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    private lateinit var viewModel: UsersViewModel
    private val fetchUsersUseCase: FetchUsersUseCase = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = UsersViewModel(fetchUsersUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `users flow should emit paging data when use case returns data`() = runTest {
        // Arrange
        val fakeUsers = listOf(
            UserModel(1, "Alice"),
            UserModel(2, "Bob"),
            UserModel(3, "Charlie")
        )
        val pagingData = PagingData.from(fakeUsers)

        coEvery { fetchUsersUseCase.invoke() } returns flowOf(pagingData)

        // Act
        advanceUntilIdle()
        val result = viewModel.users.asSnapshot()

        // Assert
        assertEquals(fakeUsers.size, result.size)
        assertEquals("Alice", result[0].login)
        assertEquals("Bob", result[1].login)
    }
}
