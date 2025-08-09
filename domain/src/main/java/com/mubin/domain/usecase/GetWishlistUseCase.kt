package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for fetching all movies currently marked as in the wishlist.
 *
 * Encapsulates domain logic for wishlist retrieval,
 * separating UI from data source concerns.
 *
 * @property repository The [MovieRepository] to fetch wishlist movies from.
 */
class GetWishlistUseCase(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to get wishlist movies.
     *
     * @return List of movies marked as in the wishlist.
     */
    suspend operator fun invoke(): List<Movie> {
        MyImdbLogger.d("GetWishlistUseCase", "Fetching wishlist movies from repository.")
        return repository.getWishlist()
    }
}