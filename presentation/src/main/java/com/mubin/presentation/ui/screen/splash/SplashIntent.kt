package com.mubin.presentation.ui.screen.splash

import com.mubin.presentation.ui.base.UiIntent

/**
 * Represents all possible user or system actions (intents) for the Splash screen
 * following the MVI (Model-View-Intent) architecture.
 *
 * These intents are consumed by the Splash screen ViewModel to trigger
 * initialization, retry, or exit logic.
 */
sealed class SplashUiIntent : UiIntent {

    /**
     * Intent to check if this is the first app launch.
     * Typically used to trigger initial data synchronization or onboarding.
     */
    data object CheckFirstLaunch : SplashUiIntent()

    /**
     * Intent to retry data synchronization if the initial attempt failed.
     */
    data object RetrySync : SplashUiIntent()

    /**
     * Intent to exit the application from the splash screen.
     */
    data object ExitApp : SplashUiIntent()
}