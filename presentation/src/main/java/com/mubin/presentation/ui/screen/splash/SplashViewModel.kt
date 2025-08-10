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

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncMoviesIfNeededUseCase: SyncMoviesIfNeededUseCase,
    private val getTotalMovieCount: GetTotalMovieCountUseCase,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: SplashUiIntent) {
        when (intent) {
            SplashUiIntent.CheckFirstLaunch -> checkFirstLaunch()
            SplashUiIntent.RetrySync -> retrySync()
            SplashUiIntent.ExitApp -> { /* handled externally */ }
        }
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val count = getTotalMovieCount()
            val isFirstLaunch = count == 0
            if (isFirstLaunch && !networkHelper.hasInternetConnection()) {
                _uiState.update {
                    it.copy(isFirstLaunch = true, showNoInternetDialog = true, isLoading = false)
                }
            } else {
                syncData()
            }
        }
    }

    private fun retrySync() {
        if (!networkHelper.hasInternetConnection()) {
            _uiState.update { it.copy(showNoInternetDialog = true) }
            return
        }
        syncData()
    }

    private fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showNoInternetDialog = false, error = null) }
            when (val result = syncMoviesIfNeededUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isDataSynced = true, isLoading = false) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }

                NetworkResult.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}