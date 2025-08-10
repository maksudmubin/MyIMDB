package com.mubin.presentation.ui.screen.movie_details

sealed interface MovieDetailsIntent {
    data class LoadMovie(val id: Int) : MovieDetailsIntent
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : MovieDetailsIntent
}