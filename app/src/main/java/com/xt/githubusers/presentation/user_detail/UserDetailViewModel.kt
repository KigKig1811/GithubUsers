package com.xt.githubusers.presentation.user_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.usecase.FetchUserDetailsUseCase
import com.xt.githubusers.utils.BaseViewModel
import com.xt.githubusers.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchUserDetailsUseCase: FetchUserDetailsUseCase
) : BaseViewModel() {

    private val userName: String = checkNotNull(savedStateHandle["selectedUser"]) {
        "UserName is required!"
    }

    private val _uiState = MutableStateFlow<Result<UserModel>>(Result.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        Timber.d("KKK Injected userName: $userName")
        getUserDetail()
    }

    private fun getUserDetail() {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            val result = fetchUserDetailsUseCase.invoke(userName = userName)
            _uiState.value = result
        }
    }

}