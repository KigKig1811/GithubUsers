package com.xt.githubusers.domain.usecase

import com.xt.githubusers.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * `FetchUsersUseCase` is a Use Case responsible for fetching a list of users from the repository.
 *
 * @property repository The `UserRepository` instance used to fetch the user data.
 * @constructor Creates a `FetchUsersUseCase` with the provided `UserRepository`.
 *
 */
@Singleton
class FetchUsersUseCase
@Inject
constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val response = repository.fetchUsers(perPage = 20)
        response
    }
}
