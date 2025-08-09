package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for updating the wishlist status of a movie.
 *
 * Encapsulates the logic for marking a movie as in or out of the wishlist,
 * allowing other layers to remain unaware of repository details.
 *
 * @property repository The [MovieRepository] to update wishlist status.
 */
class UpdateWishlistStatusUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to update the wishlist status of a movie.
     *
     * @param id The unique ID of the movie.
     * @param status The new wishlist status (true = added, false = removed).
     */
    suspend operator fun invoke(id: Int, status: Boolean) {
        MyImdbLogger.d("UpdateWishlistStatusUseCase", "Updating wishlist status for movie id=$id to $status.")
        repository.updateWishlistStatus(id, status)
    }
}