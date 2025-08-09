package com.mubin.presentation.ui.state

import com.mubin.domain.model.Movie


data class HomeUIState(
    val isLoading: Boolean = true,
    val isDataSynced: Boolean = false,
    val movieList: List<Movie> = emptyList(),
    val wishlist: List<Movie> = emptyList(),
    val selectedGenre: String? = null,
    val genres: List<String> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)