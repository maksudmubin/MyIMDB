package com.mubin.presentation.ui.screen.wishlist

import com.mubin.domain.model.Movie

data class WishlistUIState(
    val wishlist: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)