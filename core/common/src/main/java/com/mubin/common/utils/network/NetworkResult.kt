package com.mubin.common.utils.network

import com.mubin.common.utils.logger.MyImdbLogger

/**
 * A sealed class representing the result of a network operation in the MyIMDB app.
 *
 * @param T The type of data expected when the network call succeeds.
 *
 * This class has three possible states:
 * 1. [Success] - Represents a successful network response containing data.
 * 2. [Error] - Represents a failed network response with an error message, status code, and [Throwable] for debugging.
 * 3. [Loading] - Represents a loading state while the network request is in progress.
 *
 * Usage example:
 * ```
 * when (val result = repository.getMovies()) {
 *     is NetworkResult.Success -> { /* handle success */ }
 *     is NetworkResult.Error -> { /* handle error */ }
 *     is NetworkResult.Loading -> { /* show loading */ }
 * }
 * ```
 */
sealed class NetworkResult<out T> {

    /**
     * Represents a successful network response.
     *
     * @param T The type of data returned by the network call.
     * @property data The successfully retrieved data.
     */
    data class Success<out T>(val data: T) : NetworkResult<T>() {
        init {
            // Log the success state with data information
            MyImdbLogger.d("NetworkResult", "Success: Data of type ${data!!::class.simpleName} received.")
        }
    }

    /**
     * Represents an error that occurred during a network call.
     *
     * @property message The error message, typically a user-readable string.
     * @property code Optional HTTP status code from the server.
     * @property throwable Optional [Throwable] object for debugging purposes.
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>() {
        init {
            // Log the error state with relevant debug details
            MyImdbLogger.d(
                "NetworkResult",
                "Error: message='$message', code=${code ?: "N/A"}, throwable=${throwable?.javaClass?.simpleName ?: "None"}"
            )
        }
    }

    /**
     * Represents a loading state while the network request is ongoing.
     */
    object Loading : NetworkResult<Nothing>() {
        init {
            // Log when loading starts
            MyImdbLogger.d("NetworkResult", "Loading: Network request in progress...")
        }
    }
}
