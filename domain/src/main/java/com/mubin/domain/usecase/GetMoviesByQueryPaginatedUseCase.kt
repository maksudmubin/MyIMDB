package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching a paginated list of movies filtered by a search query on the title.
 *
 * Encapsulates the domain logic for query-based movie retrieval with pagination,
 * keeping UI and data layers decoupled.
 *
 * @property repository The [MovieRepository] to fetch movies from.
 */
class GetMoviesByQueryPaginatedUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get movies filtered by a search query with pagination.
     *
     * @param query The search query string.
     * @param limit The maximum number of movies to return.
     * @param offset The offset for pagination.
     * @return List of movies matching the search query.
     */
    suspend operator fun invoke(query: String, limit: Int, offset: Int): List<Movie> {
        MyImdbLogger.d("GetMoviesByQueryPaginatedUseCase", "Fetching movies with query=\"$query\", limit=$limit, offset=$offset.")
        return repository.getMoviesByQueryPaginated(query, limit, offset)
    }
}