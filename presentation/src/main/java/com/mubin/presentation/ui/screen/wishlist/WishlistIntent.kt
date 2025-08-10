package com.mubin.presentation.ui.screen.wishlist

sealed class WishlistIntent {
    object LoadWishlist : WishlistIntent()
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : WishlistIntent()
}