package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository

class UpdateWishlistStatusUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Int, status: Boolean) {
        repository.updateWishlistStatus(id, status)
    }
}