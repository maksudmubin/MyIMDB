package com.mubin.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mubin.database.entity.MovieEntity

/**
 * Data Access Object (DAO) for managing movie-related database operations in the MyIMDB app.
 *
 * This interface defines all CRUD operations for the "movies" table.
 * Methods here are marked as `suspend` to be called from coroutines, ensuring
 * database operations run off the main thread.
 */
@Dao
interface MovieDao {

    /**
     * Inserts a list of movies into the database.
     * If a movie already exists (same [MovieEntity.id]), it will be replaced.
     *
     * @param movies List of movies to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    /**
     * Returns the total number of movies stored in the database.
     *
     * @return The count of movies.
     */
    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    /**
     * Retrieves a paginated list of movies ordered by year (descending).
     *
     * @param limit Number of movies per page.
     * @param offset Offset for pagination.
     */
    @Query("SELECT * FROM movies ORDER BY year DESC LIMIT :limit OFFSET :offset")
    suspend fun getMoviesPaginated(limit: Int, offset: Int): List<MovieEntity>

    /**
     * Retrieves a paginated list of movies filtered by a specific genre.
     *
     * @param genre The genre to filter by.
     * @param limit Number of movies per page.
     * @param offset Offset for pagination.
     */
    @Query("""
    SELECT * FROM movies 
    WHERE genres LIKE '%' || :genre || '%' 
    ORDER BY year DESC 
    LIMIT :limit OFFSET :offset
    """)
    suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<MovieEntity>

    /**
     * Searches for movies whose titles contain the given query string.
     * Results are paginated and ordered by year (descending).
     *
     * @param query Search term (case-insensitive).
     * @param limit Number of movies per page.
     * @param offset Offset for pagination.
     */
    @Query("""
    SELECT * FROM movies
    WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'
    ORDER BY year DESC
    LIMIT :limit OFFSET :offset
    """)
    suspend fun searchMoviesPaginated(query: String, limit: Int, offset: Int): List<MovieEntity>

    /**
     * Searches for movies filtered by both genre and title.
     * Results are paginated and ordered by year (descending).
     *
     * @param genre The genre to filter by.
     * @param query Search term for the title (case-insensitive).
     * @param limit Number of movies per page.
     * @param offset Offset for pagination.
     */
    @Query("""
    SELECT * FROM movies 
    WHERE genres LIKE '%' || :genre || '%' AND LOWER(title) LIKE '%' || LOWER(:query) || '%'
    ORDER BY year DESC
    LIMIT :limit OFFSET :offset
    """)
    suspend fun searchMoviesByGenrePaginated(
        genre: String,
        query: String,
        limit: Int,
        offset: Int
    ): List<MovieEntity>

    /**
     * Retrieves a single movie by its unique ID.
     *
     * @param id The ID of the movie.
     * @return The [MovieEntity] if found, otherwise null.
     */
    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    /**
     * Updates the wishlist status of a movie.
     *
     * @param id The ID of the movie to update.
     * @param status The new wishlist status (true if in wishlist, false otherwise).
     */
    @Query("UPDATE movies SET isInWishlist = :status WHERE id = :id")
    suspend fun updateWishlistStatus(id: Int, status: Boolean)

    /**
     * Checks if the given movie is currently in the wishlist.
     *
     * @param id The ID of the movie to check.
     * @return True if the movie is in the wishlist, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :id AND isInWishlist = 1)")
    suspend fun isMovieInWishlist(id: Int): Boolean

    /**
     * Retrieves all movies currently in the wishlist, ordered by year (descending).
     *
     * @return List of movies marked as in the wishlist.
     */
    @Query("SELECT * FROM movies WHERE isInWishlist = 1 ORDER BY year DESC")
    suspend fun getWishlist(): List<MovieEntity>

    /**
     * Returns the total number of movies currently in the wishlist.
     *
     * @return The count of movies in the wishlist.
     */
    @Query("SELECT COUNT(*) FROM movies WHERE isInWishlist = 1")
    suspend fun getWishlistCount() : Int
}