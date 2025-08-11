package com.mubin.presentation.ui.screen.splash

import com.mubin.presentation.ui.base.UiState

/**
 * Immutable UI state for the Splash screen in the MVI (Model-View-Intent) architecture.
 *
 * Contains information about the splash loading process,
 * data synchronization status, and error handling.
 *
 * @property isLoading Indicates whether the splash screen is currently loading or syncing data.
 * @property isDataSynced Indicates whether the required data synchronization has completed successfully.
 * @property isFirstLaunch Indicates if this is the app’s first launch (used for onboarding or initial setup).
 * @property error Optional error message if synchronization or initialization fails.
 * @property showNoInternetDialog Flag to control showing a no-internet connectivity dialog.
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val isDataSynced: Boolean = false,
    val isFirstLaunch: Boolean = false,
    val error: String? = null,
    val showNoInternetDialog: Boolean = false
) : UiState