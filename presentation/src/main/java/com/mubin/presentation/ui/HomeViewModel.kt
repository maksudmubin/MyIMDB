package com.mubin.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.model.Movie
import com.mubin.domain.usecase.GetAllGenresUseCase
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.GetMoviesByGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryAndGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryPaginatedUseCase
import com.mubin.domain.usecase.GetMoviesPaginatedUseCase
import com.mubin.domain.usecase.GetTotalMovieCountUseCase
import com.mubin.domain.usecase.GetWishlistUseCase
import com.mubin.domain.usecase.SyncMoviesIfNeededUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import com.mubin.presentation.ui.state.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing UI state and business logic of the Home screen.
 *
 * Handles syncing initial data, paginated loading of movies, filtering by genre and query,
 * wishlist updates, and error handling.
 *
 * @property syncMoviesIfNeeded Use case to sync movies from remote API if local cache is empty.
 * @property getTotalMovieCount Use case to get total count of stored movies.
 * @property getMoviesPaginated Use case to load paginated movies.
 * @property getMoviesByGenrePaginated Use case to load movies filtered by genre with pagination.
 * @property getMoviesByQueryPaginated Use case to load movies filtered by search query with pagination.
 * @property getMoviesByQueryAndGenrePaginated Use case for combined filtering by genre and query with pagination.
 * @property getMovieById Use case to fetch a movie by its ID.
 * @property updateWishlistStatus Use case to update a movie's wishlist status.
 * @property getWishlist Use case to fetch movies in the wishlist.
 * @property getAllGenres Use case to fetch all available genres.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val syncMoviesIfNeeded: SyncMoviesIfNeededUseCase,
    private val getTotalMovieCount: GetTotalMovieCountUseCase,
    private val getMoviesPaginated: GetMoviesPaginatedUseCase,
    private val getMoviesByGenrePaginated: GetMoviesByGenrePaginatedUseCase,
    private val getMoviesByQueryPaginated: GetMoviesByQueryPaginatedUseCase,
    private val getMoviesByQueryAndGenrePaginated: GetMoviesByQueryAndGenrePaginatedUseCase,
    private val getMovieById: GetMovieByIdUseCase,
    private val updateWishlistStatus: UpdateWishlistStatusUseCase,
    private val getWishlist: GetWishlistUseCase,
    private val getAllGenres: GetAllGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 10

    init {
        MyImdbLogger.d("HomeViewModel", "Initialized, starting initial sync.")
        syncInitialData()
    }

    /**
     * Synchronizes movie data from remote if needed, updates UI state accordingly.
     */
    fun syncInitialData() {
        viewModelScope.launch {
            MyImdbLogger.d("HomeViewModel", "Starting syncInitialData()")
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = syncMoviesIfNeeded()) {
                is NetworkResult.Success -> {
                    MyImdbLogger.d("HomeViewModel", "Sync successful")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDataSynced = true,
                            error = null
                        )
                    }
                    loadGenres()
                    loadNextMovies()
                    loadWishlist()
                }

                is NetworkResult.Error -> {
                    MyImdbLogger.d("HomeViewModel", "Sync failed: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                        )
                    }
                }

                NetworkResult.Loading -> {
                    MyImdbLogger.d("HomeViewModel", "Sync loading state")
                }
            }
        }
    }

    /**
     * Checks if this is the first app launch by checking local movie count.
     *
     * @param onResult Callback returning true if first launch (no local movies), false otherwise.
     */
    fun checkIfFirstLaunch(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = getTotalMovieCount()
            MyImdbLogger.d("HomeViewModel", "checkIfFirstLaunch: movie count = $count")
            onResult(count == 0)
        }
    }

    /**
     * Loads all genres from repository and updates UI state.
     */
    private fun loadGenres() {
        viewModelScope.launch {
            val genres = getAllGenres()
            MyImdbLogger.d("HomeViewModel", "Loaded genres: $genres")
            _uiState.update { it.copy(genres = genres) }
        }
    }

    /**
     * Loads the next page of movies according to current filters and pagination.
     */
    fun loadNextMovies() {
        viewModelScope.launch {
            val state = _uiState.value

            val newMovies = when {
                state.searchQuery.isNotBlank() && state.selectedGenre != null -> {
                    MyImdbLogger.d("HomeViewModel", "Loading movies by query & genre: query='${state.searchQuery}', genre='${state.selectedGenre}', offset=$currentOffset")
                    getMoviesByQueryAndGenrePaginated(
                        genre = state.selectedGenre,
                        query = state.searchQuery,
                        limit = pageSize,
                        offset = currentOffset
                    )
                }
                state.searchQuery.isNotBlank() -> {
                    MyImdbLogger.d("HomeViewModel", "Loading movies by query: '${state.searchQuery}', offset=$currentOffset")
                    getMoviesByQueryPaginated(state.searchQuery, pageSize, currentOffset)
                }
                state.selectedGenre != null -> {
                    MyImdbLogger.d("HomeViewModel", "Loading movies by genre: '${state.selectedGenre}', offset=$currentOffset")
                    getMoviesByGenrePaginated(state.selectedGenre, pageSize, currentOffset)
                }
                else -> {
                    MyImdbLogger.d("HomeViewModel", "Loading movies without filter, offset=$currentOffset")
                    getMoviesPaginated(pageSize, currentOffset)
                }
            }

            currentOffset += pageSize

            _uiState.update {
                val updatedList = it.movieList + newMovies
                MyImdbLogger.d("HomeViewModel", "Loaded ${newMovies.size} movies, total now: ${updatedList.size}")
                it.copy(
                    movieList = updatedList
                )
            }
        }
    }

    /**
     * Handles genre selection changes, resets movie list and pagination, then reloads movies.
     *
     * @param genre The selected genre or null to clear filter.
     */
    fun onGenreSelected(genre: String?) {
        MyImdbLogger.d("HomeViewModel", "Genre selected: $genre")
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedGenre = genre,
                movieList = emptyList()
            )
        }
        currentOffset = 0
        loadNextMovies()
    }

    /**
     * Handles search query input changes, resets movie list and pagination, then reloads movies.
     *
     * @param query The search query string.
     */
    fun onSearchQueryChanged(query: String) {
        MyImdbLogger.d("HomeViewModel", "Search query changed: $query")
        _uiState.update {
            it.copy(
                searchQuery = query,
                movieList = emptyList()
            )
        }
        currentOffset = 0
        loadNextMovies()
    }

    /**
     * Toggles the wishlist status of a movie and reloads the wishlist.
     *
     * @param id The movie ID.
     * @param status New wishlist status (true to add, false to remove).
     */
    fun onWishlistToggle(id: Int, status: Boolean) {
        viewModelScope.launch {
            MyImdbLogger.d("HomeViewModel", "Updating wishlist status for movie $id to $status")
            updateWishlistStatus(id, status)
            loadWishlist()
        }
    }

    /**
     * Loads the wishlist movies from repository and updates UI state.
     */
    private fun loadWishlist() {
        viewModelScope.launch {
            val wishlist = getWishlist()
            MyImdbLogger.d("HomeViewModel", "Loaded wishlist with ${wishlist.size} movies.")
            _uiState.update { it.copy(wishlist = wishlist) }
        }
    }

    /**
     * Fetches a movie by its ID and returns it via callback.
     *
     * @param id Movie ID to fetch.
     * @param onResult Callback returning the movie or null if not found.
     */
    fun getMovieById(id: Int, onResult: (Movie?) -> Unit) {
        viewModelScope.launch {
            val movie = getMovieById(id)
            MyImdbLogger.d("HomeViewModel", "Fetched movie by ID $id: ${movie?.title ?: "Not Found"}")
            onResult(movie)
        }
    }

    /**
     * Clears any error message in the UI state.
     */
    fun clearError() {
        MyImdbLogger.d("HomeViewModel", "Clearing error message.")
        _uiState.update { it.copy(error = null) }
    }
}