package com.mubin.presentation.ui.screen.movie_list

import com.mubin.domain.model.Movie

/**
 * Immutable UI state for the Movie List screen in the MVI (Model-View-Intent) architecture.
 *
 * Holds all data necessary to render the movie list screen at any given moment,
 * including filtering, search, view type, and wishlist information.
 *
 * @property movies The current list of movies displayed, filtered by genre and search query.
 * @property genres The list of available genres for filtering.
 * @property selectedGenre The currently selected genre filter, or `null` if no filter is applied.
 * @property searchQuery The current search query string entered by the user.
 * @property wishlist The list of movies marked as wishlist by the user.
 * @property isLoading Indicates if the movie list or related data is currently loading.
 * @property error Optional error message to display if loading fails.
 * @property isGridView `true` if movies are shown in a grid layout, `false` for list layout.
 */
data class MovieListState(
    val movies: List<Movie> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val searchQuery: String = "",
    val wishlist: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGridView: Boolean = true
)