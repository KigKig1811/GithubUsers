package com.xt.githubusers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.xt.githubusers.data.datasource.UserRemoteMediator
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.domain.model.UserModel
import com.xt.githubusers.domain.repository.UserRepository
import com.xt.githubusers.utils.BaseRepository
import com.xt.githubusers.utils.Result
import com.xt.githubusers.utils.Result.Loading.map
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UserRepositoryImpl
@Inject
constructor(
    private val userApiService: UserApiService,
    private val userDao: UserDao,
    private val remoteKeyDao: RemoteKeyDao
) : UserRepository, BaseRepository() {

    override suspend fun fetchUsers(perPage: Int) =
        Pager(
            config = PagingConfig(
                pageSize = perPage,
                enablePlaceholders = false,
                prefetchDistance = perPage / 2,
                initialLoadSize = perPage * 2
            ),
            remoteMediator = UserRemoteMediator(
                userDao = userDao,
                remoteKeyDao = remoteKeyDao,
                userApiService = userApiService
            ),
            pagingSourceFactory = {
                userDao.fetchUsers()
            },
        ).flow.map { pagingData ->
            pagingData.map { UserMapper.entityToModel(entity = it) }
        }

    override suspend fun getUserDetail(userName: String): Result<UserModel> {
        return safeApiCall { userApiService.getUserDetail(username = userName) }
            .map { UserMapper.toModel(dto = it) }
    }
}