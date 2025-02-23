package com.xt.githubusers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.xt.githubusers.data.datasource.UserSource
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.repository.UserRepository
import com.xt.githubusers.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(
    private val userApiService: UserApiService,
) : UserRepository {

    override suspend fun fetchUsers(perPage: Int): Flow<PagingData<UserModel>> {
        return Pager(
            config = PagingConfig(pageSize = perPage),
            pagingSourceFactory = {
                UserSource(userApiService, perPage)
            }
        ).flow
    }

    override suspend fun getUserDetail(): UserModel {
        return UserModel()
    }
}