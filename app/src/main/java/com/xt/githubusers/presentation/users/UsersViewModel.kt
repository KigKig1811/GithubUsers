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

    private val _users = MutableStateFlow<PagingData<UserModel>>(PagingData.empty())
    val users = _users.asStateFlow()

    init {
        Timber.d("UsersViewModel init in: ${System.currentTimeMillis()}")
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            fetchUsersUseCase.invoke()
                .cachedIn(viewModelScope)
                .collect { data ->
                    _users.value = data
                }
        }
    }
}
