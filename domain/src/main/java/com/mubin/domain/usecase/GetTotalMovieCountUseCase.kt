package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for retrieving the total count of movies stored locally.
 *
 * Provides a clean interface for the UI or other layers to obtain
 * the movie count without dealing with repository details.
 *
 * @property repository The [MovieRepository] to fetch the count from.
 */
class GetTotalMovieCountUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get the total movie count.
     *
     * @return The total number of movies stored locally.
     */
    suspend operator fun invoke(): Int {
        MyImdbLogger.d("GetTotalMovieCountUseCase", "Fetching total movie count.")
        return repository.getTotalMovieCount()
    }
}