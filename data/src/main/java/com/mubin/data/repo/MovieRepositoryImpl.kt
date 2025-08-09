package com.mubin.data.repo

import com.mubin.common.utils.network.NetworkResult
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.database.entity.GenreEntity
import com.mubin.database.entity.MovieEntity
import com.mubin.domain.model.Movie
import com.mubin.domain.repo.MovieRepository
import com.mubin.network.model.MovieItem
import com.mubin.network.service.MovieApiService
import com.mubin.network.util.executeApiRequest

class MovieRepositoryImpl(
    private val api: MovieApiService,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao
) : MovieRepository {

    override suspend fun syncMoviesIfNeeded(): NetworkResult<Unit> {
        val localCount = movieDao.getMovieCount()
        if (localCount > 0) {
            return NetworkResult.Success(Unit) // No need to sync
        }

        return when (val response = executeApiRequest { api.fetchMovies() }) {
            is NetworkResult.Success -> {
                val baseResponse = response.data

                val movieEntities = baseResponse.movies.map { it.toEntity() }
                val genreEntities = baseResponse.genres.map { GenreEntity(name = it) }

                movieDao.insertAll(movieEntities)
                genreDao.insertAll(genreEntities)

                NetworkResult.Success(Unit)
            }

            is NetworkResult.Error -> NetworkResult.Error(
                message = response.message,
                code = response.code,
                throwable = response.throwable
            )

            NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    override suspend fun getTotalMovieCount(): Int {
        return movieDao.getMovieCount()
    }

    override suspend fun getMoviesPaginated(limit: Int, offset: Int): List<Movie> {
        return movieDao.getMoviesPaginated(limit, offset).map { it.toDomain() }
    }

    override suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<Movie> {
        return movieDao.getMoviesByGenrePaginated(genre, limit, offset).map { it.toDomain() }
    }

    override suspend fun getMoviesByQueryPaginated(query: String, limit: Int, offset: Int): List<Movie> {
        return  movieDao.searchMoviesPaginated(query, limit, offset).map { it.toDomain() }
    }

    override suspend fun getMoviesByQueryAndGenrePaginated(genre: String, query: String, limit: Int, offset: Int): List<Movie> {
        return movieDao.searchMoviesByGenrePaginated(genre, query, limit, offset).map { it.toDomain() }
    }

    override suspend fun getMovieById(id: Int): Movie? {
        return movieDao.getMovieById(id)?.toDomain()
    }

    override suspend fun updateWishlistStatus(id: Int, status: Boolean) {
        movieDao.updateWishlistStatus(id, status)
    }

    override suspend fun getWishlist(): List<Movie> {
        return movieDao.getWishlist().map { it.toDomain() }
    }

    override suspend fun getAllGenres(): List<String> {
        return genreDao.getAllGenres().map { it.toDomain() }
    }

    fun MovieItem.toEntity(): MovieEntity = MovieEntity(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        genres = genres,
        director = director,
        actors = actors,
        plot = plot,
        posterUrl = posterUrl,
        isInWishlist = false
    )

    fun MovieEntity.toDomain(): Movie = Movie(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        genres = genres,
        director = director,
        actors = actors,
        plot = plot,
        posterUrl = posterUrl,
        isInWishlist = isInWishlist
    )

    fun GenreEntity.toDomain(): String = name

}