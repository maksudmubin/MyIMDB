package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching a paginated list of movies sorted by year descending.
 *
 * Encapsulates the domain logic for paginated retrieval of movies,
 * providing a clean API for the UI and other consumers.
 *
 * @property repository The [MovieRepository] to fetch movies from.
 */
class GetMoviesPaginatedUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get paginated movies.
     *
     * @param limit The maximum number of movies to return.
     * @param offset The starting position for pagination.
     * @return List of movies for the requested page.
     */
    suspend operator fun invoke(limit: Int, offset: Int): List<Movie> {
        MyImdbLogger.d("GetMoviesPaginatedUseCase", "Fetching movies with limit=$limit, offset=$offset.")
        return repository.getMoviesPaginated(limit, offset)
    }
}