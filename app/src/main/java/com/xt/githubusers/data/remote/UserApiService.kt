package com.xt.githubusers.data.remote

import com.xt.githubusers.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("users")
    suspend fun fetchUsers(
      @Query("since") since: Int,
      @Query("per_page") perPage: Int
    ): Response<List<UserDto>>

    @GET("users/{login_username}")
    suspend fun getUserDetail(
        @Path("login_username") username: String,
    ): Response<UserDto>
}