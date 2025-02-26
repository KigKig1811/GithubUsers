package com.xt.githubusers

import androidx.lifecycle.SavedStateHandle
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.usecase.FetchUserDetailsUseCase
import com.xt.githubusers.presentation.user_detail.UserDetailViewModel
import com.xt.githubusers.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private lateinit var viewModel: UserDetailViewModel
    private val fetchUserDetailsUseCase: FetchUserDetailsUseCase = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle(mapOf("selectedUser" to "testUser"))

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = UserDetailViewModel(savedStateHandle, fetchUserDetailsUseCase)
    }

    @Test
    fun `init should call getUserDetail`() = runTest {
        // Arrange
        coEvery { fetchUserDetailsUseCase.invoke("testUser") } returns Result.Success(UserModel(1, "testUser"))

        // Act
        advanceUntilIdle()

        // Assert
        coVerify { fetchUserDetailsUseCase.invoke("testUser") }
    }


    @Test
    fun `init should call getUserDetail and emit success state`() = runTest {
        // Arrange
        val user = UserModel(1, "testUser")
        coEvery { fetchUserDetailsUseCase.invoke("testUser") } returns Result.Success(user)

        // Act
        viewModel.getUserDetail()
        advanceUntilIdle()

        // Assert
        val result = viewModel.uiState.value
        assertEquals(user, (result as Result.Success).data)
    }

    @Test
    fun `getUserDetail should emit error state when use case fails`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { fetchUserDetailsUseCase.invoke("testUser") } returns Result.Error(exception.message ?: "")

        // Act
        viewModel.getUserDetail()
        advanceUntilIdle()

        // Assert
        assertEquals(Result.Error(exception.message ?: ""), viewModel.uiState.value)
    }

    @Test
    fun `ViewModel should get userName from SavedStateHandle`() {
        // Act
        val actualUserName = viewModel.javaClass.getDeclaredField("userName").apply {
            isAccessible = true
        }.get(viewModel) as String

        // Assert
        assertEquals("testUser", actualUserName)
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if selectedUser is missing from SavedStateHandle`() {
        // Arrange
        val invalidSavedStateHandle = SavedStateHandle()

        // Act
        UserDetailViewModel(invalidSavedStateHandle, fetchUserDetailsUseCase)
    }

}