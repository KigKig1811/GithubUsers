package com.xt.githubusers.domain.usecase

import com.xt.githubusers.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchUserDetailsUseCase
@Inject
constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userName: String) = withContext(Dispatchers.IO) {
        repository.getUserDetail(userName = userName)
    }
}