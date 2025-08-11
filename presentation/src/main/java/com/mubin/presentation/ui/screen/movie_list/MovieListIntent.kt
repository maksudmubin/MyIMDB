package com.mubin.presentation.ui.screen.movie_list

/**
 * Represents all possible user actions (intents) on the Movie List screen
 * in the MVI (Model-View-Intent) architecture.
 *
 * Each intent describes an action initiated by the user or system
 * that the [MovieListViewModel] can handle to update the UI state.
 */
sealed class MovieListIntent {

    /**
     * Request to load the next page of movies for infinite scrolling.
     */
    object LoadNextPage : MovieListIntent()

    /**
     * Request to filter the movie list by the selected genre.
     *
     * @param genre The genre to filter by, or `null` to clear the filter.
     */
    data class SelectGenre(val genre: String?) : MovieListIntent()

    /**
     * Triggered when the search query changes.
     *
     * @param query The new search text entered by the user.
     */
    data class SearchQueryChanged(val query: String) : MovieListIntent()

    /**
     * Request to update the wishlist status of a movie.
     *
     * @param movieId The ID of the movie.
     * @param status `true` to add to wishlist, `false` to remove.
     */
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : MovieListIntent()

    /**
     * Toggles between different movie list view types
     * (e.g., grid view vs. list view).
     */
    object ToggleViewType : MovieListIntent()

    /**
     * Request to reload the movie list from the start,
     * ignoring any cached data.
     */
    object Refresh : MovieListIntent()
}