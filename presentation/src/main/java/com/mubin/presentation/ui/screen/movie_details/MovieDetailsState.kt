package com.mubin.presentation.ui.screen.movie_details

import com.mubin.domain.model.Movie

/**
 * Immutable state representation for the Movie Details screen
 * in the MVI (Model-View-Intent) architecture.
 *
 * This state contains all the information required to render
 * the Movie Details UI at any given time.
 *
 * @property movie The currently loaded [Movie] details, or `null` if not loaded yet.
 * @property isInWishlist Indicates whether the movie is currently in the user's wishlist.
 * @property wishlistCount The total number of movies in the wishlist.
 * @property isLoading `true` when data is being fetched or an action is in progress.
 * @property error An optional error message if a loading or action failure occurs.
 */
data class MovieDetailsState(
    val movie: Movie? = null,
    val isInWishlist: Boolean = false,
    val wishlistCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)