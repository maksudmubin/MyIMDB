package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching a paginated list of movies filtered by both genre and search query.
 *
 * Encapsulates the domain logic for combined filtering and pagination,
 * facilitating reuse and separation of concerns.
 *
 * @property repository The [MovieRepository] to fetch movies from.
 */
class GetMoviesByQueryAndGenrePaginatedUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get movies filtered by genre and search query with pagination.
     *
     * @param genre The genre filter.
     * @param query The search query filter.
     * @param limit Maximum number of movies to retrieve.
     * @param offset Offset position for pagination.
     * @return List of movies matching both filters.
     */
    suspend operator fun invoke(
        genre: String,
        query: String,
        limit: Int,
        offset: Int
    ): List<Movie> {
        MyImdbLogger.d(
            "GetMoviesByQueryAndGenrePaginatedUseCase",
            "Fetching movies for genre=\"$genre\", query=\"$query\", limit=$limit, offset=$offset."
        )
        return repository.getMoviesByQueryAndGenrePaginated(genre, query, limit, offset)
    }
}