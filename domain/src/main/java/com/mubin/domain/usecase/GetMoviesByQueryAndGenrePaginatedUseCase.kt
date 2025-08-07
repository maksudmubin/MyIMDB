package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.Movie

class GetMoviesByQueryAndGenrePaginatedUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(genre: String, query: String, limit: Int, offset: Int): List<Movie> {
        return repository.getMoviesByQueryAndGenrePaginated(genre, query, limit, offset)
    }
}