package com.mubin.domain.repo

import com.mubin.network.model.Movie
import com.mubin.network.util.NetworkResult

interface MovieRepository {

    suspend fun syncMoviesIfNeeded(): NetworkResult<Unit>

    suspend fun getTotalMovieCount(): Int

    suspend fun getMoviesPaginated(limit: Int, offset: Int): List<Movie>

    suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<Movie>

    suspend fun getMovieById(id: Int): Movie?

    suspend fun updateWishlistStatus(id: Int, status: Boolean)

    suspend fun getWishlist(): List<Movie>

    suspend fun getAllGenres(): List<String>
}