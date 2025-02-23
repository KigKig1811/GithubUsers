package com.xt.githubusers.utils

import retrofit2.Response

//open class BaseRepository {
//
//    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
//        return try {
//            val response = apiCall.invoke()
//            if (response.isSuccessful) {
//                Result.Success(data = response.body())
//            } else {
//                val errorBody = response.errorBody()?.string() ?: ""
//               Result.Error(message = errorBody)
//            }
//        } catch (e: Exception) {
//           Result.Error(message = e.localizedMessage ?: "")
//        }
//    }
//}

/**
 * Sealed class to represent the result of an operation, either success or error.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}