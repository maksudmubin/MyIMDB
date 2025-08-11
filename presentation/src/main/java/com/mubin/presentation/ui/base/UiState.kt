package com.mubin.presentation.ui.base

/**
 * Marker interface representing the current immutable state of a UI in the MVI (Model-View-Intent) architecture.
 *
 * Every screen or feature in the app should define its own `UiState` implementation
 * that contains all the data required to render the UI at any given time.
 *
 * Example:
 * ```
 * data class MovieListState(
 *     val movies: List<Movie> = emptyList(),
 *     val isLoading: Boolean = false,
 *     val errorMessage: String? = null
 * ) : UiState
 * ```
 */
interface UiState