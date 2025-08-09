package com.mubin.domain.usecase

import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

class GetWishlistUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<Movie> {
        return repository.getWishlist()
    }
}