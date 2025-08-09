package com.mubin.domain.usecase

import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

class GetMoviesPaginatedUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): List<Movie> {
        return repository.getMoviesPaginated(limit, offset)
    }
}