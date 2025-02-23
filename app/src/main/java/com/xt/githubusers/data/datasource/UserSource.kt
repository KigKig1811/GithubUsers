package com.xt.githubusers.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.xt.githubusers.data.mapper.UserMapper
import com.xt.githubusers.data.remote.UserApiService
import com.xt.githubusers.domain.model.UserModel

class UserSource(
    private val userApiService: UserApiService,
    private val perPage: Int
) : PagingSource<Int, UserModel>() {

    override fun getRefreshKey(state: PagingState<Int, UserModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserModel> {
        return try {
            val currentPage = params.key ?: 0
            val since = currentPage * perPage
            val response =
                userApiService.fetchUsers(since = since, perPage = 20)
            if (response.isSuccessful) {
                val users = response.body()?.map { UserMapper.toModel(it) }.orEmpty()
                LoadResult.Page(
                    data = users,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = if (users.isEmpty()) null else currentPage + 1
                )
            } else LoadResult.Error(Exception("Failed to fetch posts"))

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}