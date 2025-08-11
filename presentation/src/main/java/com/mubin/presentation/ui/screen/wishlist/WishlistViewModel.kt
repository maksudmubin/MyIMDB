package com.mubin.presentation.ui.screen.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * ViewModel for the Wishlist screen following the MVI (Model-View-Intent) architecture.
 *
 * Responsibilities:
 * - Handles user intents related to loading and updating the wishlist.
 * - Manages the [WishlistUIState] representing the wishlist content, loading, and error states.
 *
 * @property getWishlistUseCase Use case to retrieve the list of wishlist movies.
 * @property updateWishlistStatusUseCase Use case to add or remove movies from the wishlist.
 */
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistUseCase: GetWishlistUseCase,
    private val updateWishlistStatusUseCase: UpdateWishlistStatusUseCase
) : ViewModel() {

    // Backing mutable UI state flow
    private val _uiState = MutableStateFlow(WishlistUIState())

    /**
     * Public immutable UI state flow to be observed by the UI.
     */
    val uiState: StateFlow<WishlistUIState> = _uiState.asStateFlow()

    init {
        // Initial load of the wishlist when ViewModel is created
        loadWishlist()
    }

    /**
     * Handles incoming intents from the UI and routes them to appropriate handlers.
     *
     * @param intent The [WishlistIntent] representing a user action.
     */
    fun handleIntent(intent: WishlistIntent) {
        when (intent) {
            is WishlistIntent.LoadWishlist -> loadWishlist()
            is WishlistIntent.ToggleWishlist -> toggleWishlist(intent.movieId, intent.status)
        }
    }

    /**
     * Loads the wishlist asynchronously and updates the UI state accordingly.
     * Handles loading and error states.
     */
    private fun loadWishlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val wishlist = getWishlistUseCase()
                _uiState.update { it.copy(wishlist = wishlist, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    /**
     * Toggles the wishlist status of a given movie by adding or removing it.
     * After updating, it reloads the wishlist to reflect changes.
     *
     * @param movieId The ID of the movie to update.
     * @param status `true` to add to wishlist, `false` to remove.
     */
    private fun toggleWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            updateWishlistStatusUseCase(movieId, status)
            loadWishlist()
        }
    }
}