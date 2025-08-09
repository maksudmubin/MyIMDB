package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching all available movie genres from the repository.
 *
 * This encapsulates the domain logic for retrieving genres,
 * keeping the UI and other layers decoupled from data sources.
 *
 * @property repository The [MovieRepository] to fetch genres from.
 */
class GetAllGenresUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get all genres.
     *
     * @return A list of genre names as strings.
     */
    suspend operator fun invoke(): List<String> {
        MyImdbLogger.d("GetAllGenresUseCase", "Fetching all genres from repository.")
        return repository.getAllGenres()
    }
}