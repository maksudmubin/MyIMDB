package com.mubin.presentation.ui.state

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie


/**
 * Represents the UI state for the Home screen in the MyIMDB app.
 *
 * Holds loading status, data sync flag, lists of movies and wishlist,
 * current genre selection, available genres, search query, and error message.
 *
 * @property isLoading True when data is loading; false otherwise.
 * @property isDataSynced True if the movie data has been synced from the remote source.
 * @property movieList The list of movies currently displayed.
 * @property wishlist The list of movies marked as wishlist.
 * @property selectedGenre Currently selected genre filter; null if no filter.
 * @property genres List of all available genres.
 * @property searchQuery Current text in the search input.
 * @property error Error message to display; null if no error.
 */
data class HomeUIState(
    val isLoading: Boolean = true,
    val isDataSynced: Boolean = false,
    val movieList: List<Movie> = emptyList(),
    val wishlist: List<Movie> = emptyList(),
    val selectedGenre: String? = null,
    val genres: List<String> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    init {
        MyImdbLogger.d("HomeUIState", "Initialized with isLoading=$isLoading, isDataSynced=$isDataSynced, movieList size=${movieList.size}")
    }
}