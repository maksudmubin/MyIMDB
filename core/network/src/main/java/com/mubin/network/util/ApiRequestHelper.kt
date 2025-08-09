package com.mubin.network.util

import com.mubin.common.utils.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> executeApiRequest(
    crossinline apiCall: suspend () -> T
): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
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

            NetworkResult.Error(
                message = message,
                code = statusCode,
                throwable = e
            )
        } catch (e: IOException) {
            NetworkResult.Error(
                message = "Network Error – Please check your internet connection.",
                throwable = e
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Unexpected Error – ${e.localizedMessage ?: "Unknown error occurred."}",
                throwable = e
            )
        }
    }
}