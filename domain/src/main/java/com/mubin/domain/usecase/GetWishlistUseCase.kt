package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.Movie

class GetWishlistUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<Movie> {
        return repository.getWishlist()
    }
}