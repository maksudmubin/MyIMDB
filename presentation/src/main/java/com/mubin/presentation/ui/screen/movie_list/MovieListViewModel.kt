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

/**
 * ViewModel for the Movie List screen implementing the MVI (Model-View-Intent) pattern.
 *
 * Responsibilities:
 * - Handles incoming [MovieListIntent] actions from the UI.
 * - Manages the immutable [MovieListState] representing the UI.
 * - Loads movies, genres, wishlist data with pagination, filtering, and search.
 * - Updates wishlist status and toggles between grid and list views.
 *
 * @property getAllGenres Use case to fetch all available movie genres.
 * @property getMoviesPaginated Use case to fetch movies paginated without filters.
 * @property getMoviesByGenrePaginated Use case to fetch paginated movies by genre.
 * @property getMoviesByQueryPaginated Use case to fetch paginated movies by search query.
 * @property getMoviesByQueryAndGenrePaginated Use case to fetch paginated movies filtered by both genre and search query.
 * @property getWishlist Use case to retrieve the current wishlist movies.
 * @property updateWishlistStatus Use case to add or remove a movie from the wishlist.
 */
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

    // Backing mutable state flow for UI state
    private val _uiState = MutableStateFlow(MovieListState())

    /**
     * Public immutable UI state flow to be observed by the UI.
     */
    val uiState: StateFlow<MovieListState> = _uiState.asStateFlow()

    // Pagination offset for loading movies page by page
    private var currentOffset = 0

    // Number of movies per page
    private val pageSize = 10

    init {
        // Initial data load when ViewModel is created
        loadGenres()
        loadWishlist()
        loadNextPage()
    }

    /**
     * Processes incoming UI intents and routes them to appropriate handlers.
     *
     * @param intent The [MovieListIntent] representing a user or system action.
     */
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

    /**
     * Loads all available movie genres asynchronously
     * and updates the UI state.
     */
    private fun loadGenres() {
        viewModelScope.launch {
            val genres = getAllGenres()
            _uiState.update { it.copy(genres = genres) }
        }
    }

    /**
     * Loads the current wishlist movies asynchronously
     * and updates the UI state.
     */
    private fun loadWishlist() {
        viewModelScope.launch {
            val wishlistMovies = getWishlist()
            _uiState.update { it.copy(wishlist = wishlistMovies) }
        }
    }

    /**
     * Loads the next page of movies based on the current
     * filters and pagination offset.
     *
     * Prevents concurrent loads by checking if loading is already in progress.
     */
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

            // Append new movies to the existing list
            val updatedList = state.movies + newMovies
            _uiState.update { it.copy(movies = updatedList, isLoading = false) }
        }
    }

    /**
     * Handles the event when a genre is selected or cleared.
     * Resets pagination and clears movies before loading new results.
     *
     * @param genre The selected genre filter or `null` to clear filter.
     */
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

    /**
     * Handles changes to the search query.
     * Resets pagination and clears movies before loading filtered results.
     *
     * @param query The new search string entered by the user.
     */
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

    /**
     * Toggles the wishlist status of a given movie.
     * Refreshes the wishlist state after updating.
     *
     * @param movieId The ID of the movie to update.
     * @param status `true` to add to wishlist, `false` to remove.
     */
    private fun onToggleWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            try {
                updateWishlistStatus(movieId, status)
                loadWishlist() // Refresh wishlist after update
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * Toggles between grid and list view types.
     */
    private fun toggleViewType() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    /**
     * Refreshes the movie list by clearing existing data,
     * resetting pagination, and loading fresh data.
     */
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