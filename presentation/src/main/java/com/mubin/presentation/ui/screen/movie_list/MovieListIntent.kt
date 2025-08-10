package com.mubin.presentation.ui.screen.movie_list

sealed class MovieListIntent {
    object LoadNextPage : MovieListIntent()
    data class SelectGenre(val genre: String?) : MovieListIntent()
    data class SearchQueryChanged(val query: String) : MovieListIntent()
    data class ToggleWishlist(val movieId: Int, val status: Boolean) : MovieListIntent()
    object ToggleViewType : MovieListIntent()
    object Refresh : MovieListIntent()
}