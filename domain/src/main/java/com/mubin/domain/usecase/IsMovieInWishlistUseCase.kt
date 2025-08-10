package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository

/**
 * Use case to check if a movie is in the wishlist.
 *
 * Encapsulates domain logic for checking if a movie is in the wishlist,
 * separating UI from data source concerns.
 *
 * @property repository The [MovieRepository] to perform the check.
 */
class IsMovieInWishlistUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to check if a movie is in the wishlist.
     *
     * @return True if the movie is in the wishlist, false otherwise.
     */
    suspend operator fun invoke(id: Int): Boolean {
        MyImdbLogger.d("IsMovieInWishlistUseCase", "Checking if movie id=$id is in wishlist.")
        return repository.isMovieInWishlist(id)
    }
}