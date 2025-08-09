package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching a paginated list of movies filtered by genre.
 *
 * This isolates the domain logic for retrieving movies by genre with pagination,
 * making it reusable and testable independently from data layers.
 *
 * @property repository The [MovieRepository] to fetch movies from.
 */
class GetMoviesByGenrePaginatedUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get movies by genre with pagination.
     *
     * @param genre The genre to filter movies by.
     * @param limit The maximum number of movies to return.
     * @param offset The starting position for pagination.
     * @return List of movies matching the genre filter.
     */
    suspend operator fun invoke(genre: String, limit: Int, offset: Int): List<Movie> {
        MyImdbLogger.d("GetMoviesByGenrePaginatedUseCase", "Fetching movies for genre=\"$genre\" limit=$limit offset=$offset.")
        return repository.getMoviesByGenrePaginated(genre, limit, offset)
    }
}