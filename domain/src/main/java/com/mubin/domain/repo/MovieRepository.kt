package com.mubin.domain.repo

import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.model.Movie

/**
 * Repository interface defining operations related to movies in the MyIMDB app.
 *
 * All methods are suspend functions to support asynchronous and coroutine-friendly
 * operations, typically interacting with local database and remote API.
 */
interface MovieRepository {
    /**
     * Synchronizes movies and genres from the remote API if the local database is empty.
     *
     * @return [NetworkResult.Success] if sync is successful or no sync is needed,
     *         [NetworkResult.Error] if an error occurs,
     *         or [NetworkResult.Loading] during the sync process.
     */
    suspend fun syncMoviesIfNeeded(): NetworkResult<Unit>

    /**
     * Returns the total number of movies stored locally.
     *
     * @return The count of movies.
     */
    suspend fun getTotalMovieCount(): Int

    /**
     * Retrieves a paginated list of movies sorted by year descending.
     *
     * @param limit The maximum number of movies to return.
     * @param offset The offset for pagination.
     * @return List of movies for the requested page.
     */
    suspend fun getMoviesPaginated(limit: Int, offset: Int): List<Movie>

    /**
     * Retrieves a paginated list of movies filtered by genre.
     *
     * @param genre The genre to filter by.
     * @param limit The maximum number of movies to return.
     * @param offset The offset for pagination.
     * @return List of movies matching the genre filter.
     */
    suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<Movie>

    /**
     * Retrieves a paginated list of movies filtered by a search query on the title.
     *
     * @param query The search query string.
     * @param limit The maximum number of movies to return.
     * @param offset The offset for pagination.
     * @return List of movies matching the search query.
     */
    suspend fun getMoviesByQueryPaginated(query: String, limit: Int, offset: Int): List<Movie>

    /**
     * Retrieves a paginated list of movies filtered by both genre and search query.
     *
     * @param genre The genre to filter by.
     * @param query The search query string.
     * @param limit The maximum number of movies to return.
     * @param offset The offset for pagination.
     * @return List of movies matching both genre and query filters.
     */
    suspend fun getMoviesByQueryAndGenrePaginated(
        genre: String,
        query: String,
        limit: Int,
        offset: Int
    ): List<Movie>

    /**
     * Retrieves a movie by its unique ID.
     *
     * @param id The unique movie ID.
     * @return The movie if found, or null otherwise.
     */
    suspend fun getMovieById(id: Int): Movie?

    /**
     * Updates the wishlist status of a movie.
     *
     * @param id The movie ID.
     * @param status The new wishlist status (true = added, false = removed).
     */
    suspend fun updateWishlistStatus(id: Int, status: Boolean)

    /**
     * Retrieves all movies currently marked as in the wishlist.
     *
     * @return List of movies in the wishlist.
     */
    suspend fun getWishlist(): List<Movie>

    /**
     * Checks if a movie is currently in the wishlist.
     *
     * @param id The movie ID.
     * @return True if the movie is in the wishlist, false otherwise.
     */
    suspend fun isMovieInWishlist(id: Int): Boolean

    /**
     * Retrieves all genres available in the database.
     *
     * @return List of genre names as strings.
     */
    suspend fun getAllGenres(): List<String>
}