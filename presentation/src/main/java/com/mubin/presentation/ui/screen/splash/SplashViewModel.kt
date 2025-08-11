package com.mubin.presentation.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.android.network.NetworkHelper
import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.usecase.GetTotalMovieCountUseCase
import com.mubin.domain.usecase.SyncMoviesIfNeededUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Splash screen following the MVI (Model-View-Intent) architecture.
 *
 * Responsibilities:
 * - Handles splash screen intents such as checking first launch, retrying sync, and exit.
 * - Manages [SplashUiState] representing loading, data sync, errors, and connectivity status.
 * - Coordinates initial data synchronization if needed.
 *
 * @property syncMoviesIfNeededUseCase Use case to synchronize movies data if not present.
 * @property getTotalMovieCount Use case to retrieve the total number of movies stored locally.
 * @property networkHelper Utility to check network connectivity status.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncMoviesIfNeededUseCase: SyncMoviesIfNeededUseCase,
    private val getTotalMovieCount: GetTotalMovieCountUseCase,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    // Backing mutable UI state flow
    private val _uiState = MutableStateFlow(SplashUiState())

    /**
     * Public immutable UI state flow to be observed by the UI.
     */
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    /**
     * Handles incoming intents from the splash screen UI.
     *
     * @param intent The [SplashUiIntent] representing an action to process.
     */
    fun handleIntent(intent: SplashUiIntent) {
        when (intent) {
            SplashUiIntent.CheckFirstLaunch -> checkFirstLaunch()
            SplashUiIntent.RetrySync -> retrySync()
            SplashUiIntent.ExitApp -> { /* exit is handled externally */ }
        }
    }

    /**
     * Checks if this is the app's first launch by querying local movie count.
     * If first launch and no internet, shows a no-internet dialog.
     * Otherwise, triggers data synchronization.
     */
    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val count = getTotalMovieCount()
            val isFirstLaunch = count == 0
            if (isFirstLaunch && !networkHelper.hasInternetConnection()) {
                _uiState.update {
                    it.copy(
                        isFirstLaunch = true,
                        showNoInternetDialog = true,
                        isLoading = false
                    )
                }
            } else {
                syncData()
            }
        }
    }

    /**
     * Retries the data synchronization process.
     * If no internet connection, shows no-internet dialog.
     */
    private fun retrySync() {
        if (!networkHelper.hasInternetConnection()) {
            _uiState.update { it.copy(showNoInternetDialog = true) }
            return
        }
        syncData()
    }

    /**
     * Performs the data synchronization by calling the use case.
     * Updates the UI state according to success, error, or loading.
     */
    private fun syncData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    showNoInternetDialog = false,
                    error = null
                )
            }
            when (val result = syncMoviesIfNeededUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isDataSynced = true,
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
                NetworkResult.Loading -> {
                    // Optionally handle loading state if needed
                }
            }
        }
    }

    /**
     * Clears any existing error message from the UI state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}