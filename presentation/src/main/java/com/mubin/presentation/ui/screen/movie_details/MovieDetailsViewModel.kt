package com.mubin.presentation.ui.screen.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.GetTotalWishlistCountUseCase
import com.mubin.domain.usecase.IsMovieInWishlistUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Movie Details screen, following the MVI (Model-View-Intent) architecture.
 *
 * This ViewModel:
 * - Consumes [MovieDetailsIntent] events from the UI.
 * - Updates the immutable [MovieDetailsState] to reflect data changes.
 * - Uses injected use cases to retrieve and update movie details and wishlist status.
 *
 * @property getMovieById Use case to fetch a movie by its ID.
 * @property updateWishlistStatus Use case to add or remove a movie from the wishlist.
 * @property isMovieInWishlist Use case to check if a given movie is in the wishlist.
 * @property getWishlistCount Use case to retrieve the total number of wishlist items.
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieById: GetMovieByIdUseCase,
    private val updateWishlistStatus: UpdateWishlistStatusUseCase,
    private val isMovieInWishlist: IsMovieInWishlistUseCase,
    private val getWishlistCount: GetTotalWishlistCountUseCase
) : ViewModel() {

    // Backing state for the UI
    private val _uiState = MutableStateFlow(MovieDetailsState())

    /**
     * Public immutable state flow for observing UI state changes.
     */
    val uiState: StateFlow<MovieDetailsState> = _uiState.asStateFlow()

    /**
     * Handles user or system actions (intents) for the Movie Details screen.
     *
     * @param intent The [MovieDetailsIntent] to process.
     */
    fun handleIntent(intent: MovieDetailsIntent) {
        when (intent) {
            is MovieDetailsIntent.LoadMovie -> {
                loadMovieById(intent.id)
                loadWishlistCount()
            }
            is MovieDetailsIntent.ToggleWishlist -> {
                updateWishlist(intent.movieId, intent.status)
            }
        }
    }

    /**
     * Loads a movie by its [id] and updates the UI state with
     * the movie details and wishlist status.
     *
     * - Sets `isLoading` to true before fetching.
     * - Handles exceptions and updates `error` on failure.
     */
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

    /**
     * Updates the wishlist status for a movie.
     *
     * @param movieId The movie to update.
     * @param status `true` to add to wishlist, `false` to remove.
     *
     * - Calls [updateWishlistStatus] to persist the change.
     * - Updates the `isInWishlist` state if the current movie matches [movieId].
     * - Refreshes the wishlist count.
     */
    private fun updateWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            updateWishlistStatus(movieId, status)
            _uiState.update { currentState ->
                if (currentState.movie?.id == movieId) {
                    currentState.copy(isInWishlist = status)
                } else currentState
            }
            loadWishlistCount()
        }
    }

    /**
     * Loads the current wishlist count and updates the state.
     */
    private fun loadWishlistCount() {
        viewModelScope.launch {
            val count = getWishlistCount()
            _uiState.update { it.copy(wishlistCount = count) }
        }
    }
}