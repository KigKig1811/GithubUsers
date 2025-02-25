package com.xt.githubusers.utils

import retrofit2.Response
import retrofit2.HttpException
import java.io.IOException

/**
 * Base class for handling API calls safely.
 * Provides a standardized way to make network requests and handle errors.
 */
open class BaseRepository {

    /**
     * Executes a safe API call, catching exceptions and handling HTTP errors.
     *
     * @param apiCall A suspend function that makes a network request using Retrofit.
     * @return [Result] representing either success with data or an error with a message.
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall() // Invoke the API call

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body) // API call succeeded with non-null data
                } else {
                    Result.Error("Response body is null", response.code())
                }
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                Result.Error(errorBody, response.code()) // API call failed with error message
            }
        } catch (e: IOException) {
            // Network-related exceptions (e.g., no internet connection)
            Result.Error("Network Error: ${e.message}", null)
        } catch (e: HttpException) {
            // Retrofit's HttpException for non-2xx responses
            Result.Error("HTTP Exception: ${e.message}", e.code())
        } catch (e: Exception) {
            // Any other unexpected exceptions
            Result.Error("Unexpected Error: ${e.message}", null)
        }
    }
}

/**
 * Sealed class representing the result of an operation.
 * Can be either a successful result, an error, or a loading state.
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation with a result of type [T].
     *
     * @param data The successful result data.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents an error during the operation.
     *
     * @param message A human-readable error message.
     * @param code The HTTP status code (if applicable), or null if not available.
     */
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()

    /**
     * Represents a loading state while an operation is in progress.
     * Typically used for UI updates.
     */
    data object Loading : Result<Nothing>()

    /**
     * Checks if the result is a success.
     *
     * @return `true` if this is a [Success], otherwise `false`.
     */
    fun isSuccess(): Boolean = this is Success<T>

    /**
     * Checks if the result is an error.
     *
     * @return `true` if this is an [Error], otherwise `false`.
     */
    fun isError(): Boolean = this is Error

    /**
     * Retrieves the success data, or null if the result is not successful.
     *
     * @return The success data if available, otherwise `null`.
     */
    fun getOrNull(): T? = if (this is Success) data else null

    /**
     * Transforms a [Result] of type [T] into [Result] of type [R] using [transform].
     *
     * @param transform Function to convert [T] into [R].
     * @return A new [Result] with transformed data or the same error/loading state.
     **/
    fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message, code)
            Loading -> Loading
        }
    }

}

