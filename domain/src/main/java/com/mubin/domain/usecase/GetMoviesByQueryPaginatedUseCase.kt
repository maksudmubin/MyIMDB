package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.Movie

class GetMoviesByQueryPaginatedUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, limit: Int, offset: Int): List<Movie> {
        return repository.getMoviesByQueryPaginated(query, limit, offset)
    }
}