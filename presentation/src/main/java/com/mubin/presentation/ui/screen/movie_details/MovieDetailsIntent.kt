package com.mubin.presentation.ui.screen.movie_details

/**
 * Defines all possible user or system actions (intents) for the Movie Details screen
 * in the MVI (Model-View-Intent) architecture.
 *
 * Intents represent events that request the ViewModel to perform an action
 * or update the state. They are consumed by the ViewModel to trigger
 * data loading, updates, or other side effects.
 */
sealed interface MovieDetailsIntent {

    /**
     * Intent to load a specific movie's details by its [id].
     *
     * @param id The unique identifier of the movie to load.
     */
    data class LoadMovie(val id: Int) : MovieDetailsIntent

    /**
     * Intent to add or remove a movie from the wishlist.
     *
     * @param movieId The unique identifier of the movie to update.
     * @param status `true` if the movie should be added to the wishlist,
     *               `false` if it should be removed.
     */
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : MovieDetailsIntent
}