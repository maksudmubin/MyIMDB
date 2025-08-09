package com.mubin.presentation.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Helper class to check network connectivity status.
 *
 * @property context The application context used to access system services.
 */
class NetworkHelper(private val context: Context) {

    /**
     * Checks whether the device currently has an active internet connection.
     *
     * It considers cellular, Wi-Fi, and Ethernet transports as valid connections.
     *
     * @return True if there is an active internet connection, false otherwise.
     */
    fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val hasConnection = capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))

        MyImdbLogger.d("NetworkHelper", "Internet connection status: $hasConnection")
        return hasConnection
    }
}