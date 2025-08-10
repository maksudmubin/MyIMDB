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

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistUseCase: GetWishlistUseCase,
    private val updateWishlistStatusUseCase: UpdateWishlistStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUIState())
    val uiState: StateFlow<WishlistUIState> = _uiState.asStateFlow()

    init {
        loadWishlist()
    }

    fun handleIntent(intent: WishlistIntent) {
        when (intent) {
            is WishlistIntent.LoadWishlist -> loadWishlist()
            is WishlistIntent.ToggleWishlist -> toggleWishlist(intent.movieId, intent.status)
        }
    }

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

    private fun toggleWishlist(movieId: Int, status: Boolean) {
        viewModelScope.launch {
            updateWishlistStatusUseCase(movieId, status)
            loadWishlist()
        }
    }
}