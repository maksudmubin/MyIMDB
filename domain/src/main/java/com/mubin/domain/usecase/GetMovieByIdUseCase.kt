package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.Movie

class GetMovieByIdUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Int): Movie? {
        return repository.getMovieById(id)
    }
}