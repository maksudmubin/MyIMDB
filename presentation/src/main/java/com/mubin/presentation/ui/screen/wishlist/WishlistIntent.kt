package com.mubin.presentation.ui.screen.wishlist

/**
 * Represents all possible user actions (intents) on the Wishlist screen
 * in the MVI (Model-View-Intent) architecture.
 *
 * These intents are sent from the UI to the ViewModel
 * to trigger state changes or business logic.
 */
sealed class WishlistIntent {

    /**
     * Intent to load the wishlist movies.
     */
    object LoadWishlist : WishlistIntent()

    /**
     * Intent to add or remove a movie from the wishlist.
     *
     * @param movieId The ID of the movie.
     * @param status `true` to add to wishlist, `false` to remove.
     */
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : WishlistIntent()
}