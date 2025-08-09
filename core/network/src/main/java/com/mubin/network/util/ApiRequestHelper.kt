package com.mubin.network.util

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.common.utils.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Executes a suspend API call in an IO-optimized coroutine context and wraps the result
 * in a [NetworkResult] for consistent error handling across the MyIMDB app.
 *
 * @param T The type of the successful response object.
 * @param apiCall A suspend function that performs the network request and returns the result.
 *
 * @return A [NetworkResult.Success] containing the response on success,
 *         or a [NetworkResult.Error] containing details about the failure.
 *
 * ### Error Handling:
 * - **HttpException** → Returns user-friendly messages for common HTTP status codes.
 * - **IOException** → Represents network connectivity issues.
 * - **Exception** → Catches any unexpected errors.
 *
 * ### Example usage:
 * ```
 * val result = executeApiRequest { apiService.getMovies() }
 * when (result) {
 *     is NetworkResult.Success -> { /* Handle success */ }
 *     is NetworkResult.Error -> { /* Show error */ }
 * }
 * ```
 */
suspend inline fun <T> executeApiRequest(
    crossinline apiCall: suspend () -> T
): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            MyImdbLogger.d("executeApiRequest", "Starting API request...")
            val response = apiCall()
            MyImdbLogger.d("executeApiRequest", "API request successful: ${response!!::class.simpleName}")
            NetworkResult.Success(response)

        } catch (e: HttpException) {
            val statusCode = e.code()
            val message = when (statusCode) {
                400 -> "Bad Request – The server could not understand your request."
                401 -> "Unauthorized – Please check your credentials."
                403 -> "Forbidden – You don't have access to this resource."
                404 -> "Not Found – The requested resource doesn't exist."
                408 -> "Request Timeout – The server timed out waiting for the request."
                409 -> "Conflict – Duplicate or conflicting resource."
                422 -> "Unprocessable Entity – Validation failed on submitted data."
                429 -> "Too Many Requests – You're being rate limited."
                500 -> "Internal Server Error – Something went wrong on the server."
                502 -> "Bad Gateway – Invalid response from the upstream server."
                503 -> "Service Unavailable – The server is temporarily unavailable."
                504 -> "Gateway Timeout – The server didn't respond in time."
                else -> "HTTP $statusCode – Unexpected server error."
            }
            MyImdbLogger.d("executeApiRequest", "HTTP error $statusCode: ${e.message()}")
            NetworkResult.Error(
                message = message,
                code = statusCode,
                throwable = e
            )

        } catch (e: IOException) {
            MyImdbLogger.d("executeApiRequest", "Network error: ${e.localizedMessage}")
            NetworkResult.Error(
                message = "Network Error – Please check your internet connection.",
                throwable = e
            )

        } catch (e: Exception) {
            MyImdbLogger.d("executeApiRequest", "Unexpected error: ${e.localizedMessage}")
            NetworkResult.Error(
                message = "Unexpected Error – ${e.localizedMessage ?: "Unknown error occurred."}",
                throwable = e
            )
        }
    }
}