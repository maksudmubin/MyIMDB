package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching a movie by its unique ID from the repository.
 *
 * Encapsulates the logic of retrieving a single movie entity,
 * allowing the UI or other layers to stay independent of data sources.
 *
 * @property repository The [MovieRepository] to fetch the movie from.
 */
class GetMovieByIdUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to retrieve a movie by ID.
     *
     * @param id The unique identifier of the movie.
     * @return The [Movie] if found, or null otherwise.
     */
    suspend operator fun invoke(id: Int): Movie? {
        MyImdbLogger.d("GetMovieByIdUseCase", "Fetching movie with id=$id from repository.")
        return repository.getMovieById(id)
    }
}