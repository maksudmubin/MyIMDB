package com.mubin.presentation.ui.screen.splash

import com.mubin.presentation.ui.base.UiIntent

sealed class SplashUiIntent : UiIntent {
    data object CheckFirstLaunch : SplashUiIntent()
    data object RetrySync : SplashUiIntent()
    data object ExitApp : SplashUiIntent()
}