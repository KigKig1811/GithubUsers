package com.xt.githubusers.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * An Interceptor that automatically adds an Authorization header to all API requests.
 * This ensures that every request includes the authentication token without modifying each request manually.
 */
class NetworkInterceptor @Inject constructor() : Interceptor {

    /**
     * Intercepts outgoing requests to modify them before they are sent to the server.
     * Adds an Authorization header containing the authentication token.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Create a new request with an added Authorization header
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "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") // Adds the API token
            .build()

        // Proceed with the modified request
        return chain.proceed(request)
    }
}