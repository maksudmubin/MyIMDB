package com.mubin.presentation.ui.screen.wishlist

import com.mubin.domain.model.Movie

/**
 * Immutable UI state for the Wishlist screen in the MVI (Model-View-Intent) architecture.
 *
 * Holds the current wishlist movies and UI loading/error states.
 *
 * @property wishlist The list of movies marked as wishlist by the user.
 * @property isLoading Indicates whether the wishlist data is currently loading.
 * @property error Optional error message if loading or updating the wishlist fails.
 */
data class WishlistUIState(
    val wishlist: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)