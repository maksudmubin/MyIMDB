package com.mubin.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mubin.database.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("SELECT * FROM movies LIMIT :limit OFFSET :offset")
    suspend fun getMoviesPaginated(limit: Int, offset: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE genres LIKE '%' || :genre || '%' LIMIT :limit OFFSET :offset")
    suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("UPDATE movies SET isInWishlist = :status WHERE id = :id")
    suspend fun updateWishlistStatus(id: Int, status: Boolean)

    @Query("SELECT * FROM movies WHERE isInWishlist = 1")
    suspend fun getWishlist(): List<MovieEntity>

}