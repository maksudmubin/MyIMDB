package com.mubin.domain.repo

import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.model.Movie

interface MovieRepository {

    suspend fun syncMoviesIfNeeded(): NetworkResult<Unit>

    suspend fun getTotalMovieCount(): Int

    suspend fun getMoviesPaginated(limit: Int, offset: Int): List<Movie>

    suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<Movie>

    suspend fun getMoviesByQueryPaginated(query: String, limit: Int, offset: Int): List<Movie>

    suspend fun getMoviesByQueryAndGenrePaginated(genre: String, query: String, limit: Int, offset: Int): List<Movie>

    suspend fun getMovieById(id: Int): Movie?

    suspend fun updateWishlistStatus(id: Int, status: Boolean)

    suspend fun getWishlist(): List<Movie>

    suspend fun getAllGenres(): List<String>
}