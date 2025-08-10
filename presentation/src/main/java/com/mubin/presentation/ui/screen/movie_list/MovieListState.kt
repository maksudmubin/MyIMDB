package com.mubin.presentation.ui.screen.movie_list

import com.mubin.domain.model.Movie

data class MovieListState(
    val movies: List<Movie> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val searchQuery: String = "",
    val wishlist: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGridView: Boolean = true
)