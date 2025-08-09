package com.mubin.domain.usecase

import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

class GetMovieByIdUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Int): Movie? {
        return repository.getMovieById(id)
    }
}