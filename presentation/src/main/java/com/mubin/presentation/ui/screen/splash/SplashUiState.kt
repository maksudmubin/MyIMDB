package com.mubin.presentation.ui.screen.splash

import com.mubin.presentation.ui.base.UiState

data class SplashUiState(
    val isLoading: Boolean = true,
    val isDataSynced: Boolean = false,
    val isFirstLaunch: Boolean = false,
    val error: String? = null,
    val showNoInternetDialog: Boolean = false
) : UiState