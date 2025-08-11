package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for retrieving the total count of movies in wishlist.
 *
 * Provides a clean interface for the UI or other layers to obtain
 * the wishlist count without dealing with repository details.
 *
 * @property repository The [MovieRepository] to fetch the wishlist count from.
 */
class GetTotalWishlistCountUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get the total wishlist count.
     *
     * @return The total number of movies in the wishlist.
     */
    suspend operator fun invoke(): Int {
        MyImdbLogger.d("GetTotalWishlistCountUseCase", "Fetching total wishlist count.")
        return repository.getWishlistCount()
    }
}