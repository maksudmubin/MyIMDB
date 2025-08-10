package com.mubin.presentation.ui.screen.movie_details

import com.mubin.domain.model.Movie

data class MovieDetailsState(
    val movie: Movie? = null,
    val isInWishlist: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)