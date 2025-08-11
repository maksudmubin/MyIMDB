package com.mubin.presentation.ui.base

/**
 * Marker interface representing a user or system action (intent) in the MVI (Model-View-Intent) architecture.
 *
 * `UiIntent` instances are dispatched from the UI layer to the ViewModel
 * to request state changes or trigger specific actions.
 *
 * Example:
 * ```
 * sealed interface MovieListIntent : UiIntent {
 *     object LoadMovies : MovieListIntent
 *     data class SearchMovies(val query: String) : MovieListIntent
 * }
 * ```
 */
interface UiIntent