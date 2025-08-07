package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.Movie

class GetMoviesByGenrePaginatedUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(genre: String, limit: Int, offset: Int): List<Movie> {
        return repository.getMoviesByGenrePaginated(genre, limit, offset)
    }
}