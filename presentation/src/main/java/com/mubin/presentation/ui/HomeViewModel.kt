package com.mubin.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.domain.usecase.GetAllGenresUseCase
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.GetMoviesByGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesPaginatedUseCase
import com.mubin.domain.usecase.GetTotalMovieCountUseCase
import com.mubin.domain.usecase.GetWishlistUseCase
import com.mubin.domain.usecase.SyncMoviesIfNeededUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import com.mubin.network.model.Movie
import com.mubin.network.util.NetworkResult
import com.mubin.presentation.ui.state.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val syncMoviesIfNeeded: SyncMoviesIfNeededUseCase,
    private val getTotalMovieCount: GetTotalMovieCountUseCase,
    private val getMoviesPaginated: GetMoviesPaginatedUseCase,
    private val getMoviesByGenrePaginated: GetMoviesByGenrePaginatedUseCase,
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
        syncInitialData()
    }

    private fun syncInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = syncMoviesIfNeeded()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isDataSynced = true
                        )
                    }
                    loadGenres()
                    loadNextMovies()
                    loadWishlist()
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                NetworkResult.Loading -> {}
            }
        }
    }

    fun checkIfFirstLaunch(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = getTotalMovieCount()
            onResult(count == 0)
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            val genres = getAllGenres()
            _uiState.update { it.copy(genres = genres) }
        }
    }

    fun loadNextMovies() {
        viewModelScope.launch {
            val newMovies = if (_uiState.value.selectedGenre == null) {
                getMoviesPaginated(pageSize, currentOffset)
            } else {
                getMoviesByGenrePaginated(_uiState.value.selectedGenre!!, pageSize, currentOffset)
            }

            currentOffset += pageSize

            _uiState.update {
                val updatedList = it.movieList + newMovies
                it.copy(
                    movieList = updatedList,
                    filteredMovies = applySearchFilter(updatedList, it.searchQuery)
                )
            }
        }
    }

    fun onGenreSelected(genre: String?) {
        _uiState.update {
            it.copy(
                selectedGenre = genre,
                movieList = emptyList(),
                filteredMovies = emptyList()
            )
        }
        currentOffset = 0
        loadNextMovies()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredMovies = applySearchFilter(it.movieList, query)
            )
        }
    }

    private fun applySearchFilter(list: List<Movie>, query: String): List<Movie> {
        return if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.plot.contains(query, ignoreCase = true)
        }
    }

    fun onWishlistToggle(id: Int, status: Boolean) {
        viewModelScope.launch {
            updateWishlistStatus(id, status)
            loadWishlist()
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            val wishlist = getWishlist()
            _uiState.update { it.copy(wishlist = wishlist) }
        }
    }

    fun getMovieById(id: Int, onResult: (Movie?) -> Unit) {
        viewModelScope.launch {
            onResult(getMovieById(id))
        }
    }
}