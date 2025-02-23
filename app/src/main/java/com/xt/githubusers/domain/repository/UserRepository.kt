package com.xt.githubusers.domain.repository

import androidx.paging.PagingData
import com.xt.githubusers.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUsers(perPage: Int): Flow<PagingData<UserModel>>
    suspend fun getUserDetail(): UserModel
}