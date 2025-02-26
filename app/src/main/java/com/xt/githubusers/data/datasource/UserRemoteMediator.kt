package com.xt.githubusers.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.data.room.dao.RemoteKeyDao
import com.xt.githubusers.data.room.dao.UserDao
import com.xt.githubusers.data.room.entity.RemoteKeyEntity
import com.xt.githubusers.data.room.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val userDao: UserDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val userApiService: UserApiService,
) : RemoteMediator<Int, UserEntity>() {

    /**
     * Initializes the data fetching process based on cached data freshness.
     *
     * @return [InitializeAction.SKIP_INITIAL_REFRESH] if the cached data is considered fresh,
     *         or [InitializeAction.LAUNCH_INITIAL_REFRESH] if the data is stale and needs a full refresh.
     * The cacheTimeout is set to 1 hour.
     */
    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return withContext(Dispatchers.IO) {
            if (System.currentTimeMillis() - (remoteKeyDao.getCreationTime() ?: 0) < cacheTimeout) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            // Determine the page to load based on the LoadType
            val page = when (loadType) {
                LoadType.REFRESH -> 0

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // For appending, we need to get the next key from the remote key table.
                    val remoteKey = remoteKeyDao.remoteKey(id = 0)
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            // Determine perPage size
            val perPage = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }

            val response = userApiService.fetchUsers(
                since = page * perPage,
                perPage = perPage
            ).body()?.map {
                UserMapper.toModel(dto = it)
            }.orEmpty()

            // Clear the database if it's a refresh.
            if (loadType == LoadType.REFRESH) {
                userDao.clearUsers()
                remoteKeyDao.clearRemoteKeys()
            }

            // Save the next page key based on the current page
            val nextKey = if (response.isEmpty()) null else page + 1
            remoteKeyDao.insert(key = RemoteKeyEntity(id = 0, nextKey = nextKey))

            // Insert users into the database
            userDao.insertAll(response.map { UserMapper.toEntity(user = it) })

            // Return success with endOfPaginationReached based on whether the response was empty.
            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}