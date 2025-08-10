package com.mubin.presentation.ui.screen.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.domain.model.Movie
import com.mubin.domain.usecase.GetAllGenresUseCase
import com.mubin.domain.usecase.GetMoviesByGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryAndGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryPaginatedUseCase
import com.mubin.domain.usecase.GetMoviesPaginatedUseCase
import com.mubin.domain.usecase.GetWishlistUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getAllGenres: GetAllGenresUseCase,
    private val getMoviesPaginated: GetMoviesPaginatedUseCase,
    private val getMoviesByGenrePaginated: GetMoviesByGenrePaginatedUseCase,
    private val getMoviesByQueryPaginated: GetMoviesByQueryPaginatedUseCase,
    private val getMoviesByQueryAndGenrePaginated: GetMoviesByQueryAndGenrePaginatedUseCase,
    private val getWishlist: GetWishlistUseCase,
    private val updateWishlistStatus: UpdateWishlistStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListState())
    val uiState: StateFlow<MovieListState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 10

    init {
        loadGenres()
        loadWishlist()
        loadNextPage()
    }

    fun handleIntent(intent: MovieListIntent) {
        when (intent) {
            is MovieListIntent.LoadNextPage -> loadNextPage()
            is MovieListIntent.SelectGenre -> onGenreSelected(intent.genre)
            is MovieListIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is MovieListIntent.ToggleWishlist -> onToggleWishlist(intent.movieId, intent.status)
            is MovieListIntent.ToggleViewType -> toggleViewType()
            is MovieListIntent.Refresh -> refresh()
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            val genres = getAllGenres()
            _uiState.update { it.copy(genres = genres) }
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            val wishlistMovies = getWishlist()
            _uiState.update { it.copy(wishlist = wishlistMovies) }
        }
    }

    private fun loadNextPage() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val state = _uiState.value
            val newMovies: List<Movie> = try {
                when {
                    state.searchQuery.isNotBlank() && state.selectedGenre != null -> {
                        getMoviesByQueryAndGenrePaginated(
                            genre = state.selectedGenre,
                            query = state.searchQuery,
                            limit = pageSize,
                            offset = currentOffset
                        )
                    }
                    state.searchQuery.isNotBlank() -> {
                        getMoviesByQueryPaginated(state.searchQuery, pageSize, currentOffset)
                    }
                    state.selectedGenre != null -> {
                        getMoviesByGenrePaginated(state.selectedGenre, pageSize, currentOffset)
                    }
                    else -> {
                        getMoviesPaginated(pageSize, currentOffset)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            currentOffset += pageSize

            val updatedList = state.movies + newMovies
            _uiState.update { it.copy(movies = updatedList, isLoading = false) }
        }
    }

    private fun onGenreSelected(genre: String?) {
        currentOffset = 0
        _uiState.update {
            it.copy(
                selectedGenre = genre,
                searchQuery = "",
                movies = emptyList(),
                error = null
            )
        }
        loadNextPage()
    }

    private fun onSearchQueryChanged(query: String) {
        currentOffset = 0
        _uiState.update {
            it.copy(
                searchQuery = query,
                movies = emptyList(),
                error = null
            )
        }
        loadNextPage()
    }

    private fun onToggleWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            try {
                updateWishlistStatus(movieId, status)
                loadWishlist() // Refresh wishlist state
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun toggleViewType() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    private fun refresh() {
        currentOffset = 0
        _uiState.update {
            it.copy(
                movies = emptyList(),
                isLoading = true,
                error = null
            )
        }
        loadNextPage()
    }
}