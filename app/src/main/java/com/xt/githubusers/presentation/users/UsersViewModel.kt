package com.xt.githubusers.presentation.users

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.usecase.FetchUsersUseCase
import com.xt.githubusers.utils.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsersViewModel
@Inject constructor(
    private val fetchUsersUseCase: FetchUsersUseCase
) : BaseViewModel() {

    private val _usersState = MutableStateFlow<UiState>(UiState.Loading)
    val usersState = _usersState.asStateFlow()

    init {
        Timber.d("UsersViewModel init in: ${System.currentTimeMillis()}")
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            kotlin.runCatching {
                fetchUsersUseCase.invoke()
                    .cachedIn(viewModelScope)
                    .collect { data ->
                        _usersState.value = UiState.Success(data = data)
                    }
            }.onFailure { throwable ->
                _usersState.value = UiState.Error(throwable.localizedMessage ?: "Unknown error")
            }
        }
    }
}

sealed class UiState {
    data object Loading : UiState()
    data class Success(val data: PagingData<UserModel>) : UiState()
    data class Error(val message: String) : UiState()
}
