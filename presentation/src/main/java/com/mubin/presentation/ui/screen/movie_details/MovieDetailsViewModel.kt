package com.mubin.presentation.ui.screen.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.IsMovieInWishlistUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieById: GetMovieByIdUseCase,
    private val updateWishlistStatus: UpdateWishlistStatusUseCase,
    private val isMovieInWishlist: IsMovieInWishlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsState())
    val uiState: StateFlow<MovieDetailsState> = _uiState.asStateFlow()

    fun handleIntent(intent: MovieDetailsIntent) {
        when (intent) {
            is MovieDetailsIntent.LoadMovie -> {
                loadMovieById(intent.id)
            }
            is MovieDetailsIntent.ToggleWishlist -> {
                updateWishlist(intent.movieId, intent.status)
            }
        }
    }

    private fun loadMovieById(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val movie = getMovieById(id)
                val isInWishlist = isMovieInWishlist(id)
                _uiState.update {
                    it.copy(
                        movie = movie,
                        isInWishlist = isInWishlist,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load movie"
                    )
                }
            }
        }
    }

    private fun updateWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            updateWishlistStatus(movieId, status)
            // Update wishlist flag locally without fetching whole wishlist
            _uiState.update { currentState ->
                if (currentState.movie?.id == movieId) {
                    currentState.copy(isInWishlist = status)
                } else currentState
            }
        }
    }
}