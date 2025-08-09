package com.mubin.data.repo

import com.mubin.common.utils.logger.MyImdbLogger
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

/**
 * Implementation of [MovieRepository] for managing movie data in MyIMDB app.
 *
 * This repository handles syncing data from the remote API, querying the local database,
 * and transforming entities between different layers.
 *
 * @property api The remote API service for fetching movie data.
 * @property movieDao DAO for accessing movies in the local database.
 * @property genreDao DAO for accessing genres in the local database.
 */
class MovieRepositoryImpl(
    private val api: MovieApiService,
    private val movieDao: MovieDao,
    private val genreDao: GenreDao
) : MovieRepository {

    /**
     * Synchronizes movies and genres from the remote API if the local database is empty.
     *
     * @return [NetworkResult.Success] on success or [NetworkResult.Error] on failure.
     */
    override suspend fun syncMoviesIfNeeded(): NetworkResult<Unit> {
        MyImdbLogger.d("MovieRepositoryImpl", "Checking if sync is needed.")
        val localCount = movieDao.getMovieCount()
        if (localCount > 0) {
            MyImdbLogger.d("MovieRepositoryImpl", "Local database already has $localCount movies, skipping sync.")
            return NetworkResult.Success(Unit) // No need to sync
        }

        MyImdbLogger.d("MovieRepositoryImpl", "Local database empty, syncing from API.")
        return when (val response = executeApiRequest { api.fetchMovies() }) {
            is NetworkResult.Success -> {
                val baseResponse = response.data

                MyImdbLogger.d("MovieRepositoryImpl", "API fetch successful: ${baseResponse.movies.size} movies, ${baseResponse.genres.size} genres.")

                val movieEntities = baseResponse.movies.map { it.toEntity() }
                val genreEntities = baseResponse.genres.map { GenreEntity(name = it) }

                movieDao.insertAll(movieEntities)
                genreDao.insertAll(genreEntities)

                MyImdbLogger.d("MovieRepositoryImpl", "Inserted movies and genres into local database.")
                NetworkResult.Success(Unit)
            }

            is NetworkResult.Error -> {
                MyImdbLogger.d("MovieRepositoryImpl", "API fetch failed: ${response.message} (code=${response.code})")
                NetworkResult.Error(
                    message = response.message,
                    code = response.code,
                    throwable = response.throwable
                )
            }

            NetworkResult.Loading -> {
                MyImdbLogger.d("MovieRepositoryImpl", "API request is loading.")
                NetworkResult.Loading
            }
        }
    }

    /** Returns the total number of movies stored locally. */
    override suspend fun getTotalMovieCount(): Int {
        val count = movieDao.getMovieCount()
        MyImdbLogger.d("MovieRepositoryImpl", "Total movie count fetched: $count")
        return count
    }

    /** Retrieves paginated movies sorted by year descending. */
    override suspend fun getMoviesPaginated(limit: Int, offset: Int): List<Movie> {
        val movies = movieDao.getMoviesPaginated(limit, offset).map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched ${movies.size} movies paginated (limit=$limit, offset=$offset).")
        return movies
    }

    /** Retrieves paginated movies filtered by genre. */
    override suspend fun getMoviesByGenrePaginated(genre: String, limit: Int, offset: Int): List<Movie> {
        val movies = movieDao.getMoviesByGenrePaginated(genre, limit, offset).map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched ${movies.size} movies by genre \"$genre\" paginated.")
        return movies
    }

    /** Retrieves paginated movies filtered by search query in title. */
    override suspend fun getMoviesByQueryPaginated(query: String, limit: Int, offset: Int): List<Movie> {
        val movies = movieDao.searchMoviesPaginated(query, limit, offset).map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched ${movies.size} movies by query \"$query\" paginated.")
        return movies
    }

    /** Retrieves paginated movies filtered by both genre and title query. */
    override suspend fun getMoviesByQueryAndGenrePaginated(genre: String, query: String, limit: Int, offset: Int): List<Movie> {
        val movies = movieDao.searchMoviesByGenrePaginated(genre, query, limit, offset).map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched ${movies.size} movies by genre \"$genre\" and query \"$query\" paginated.")
        return movies
    }

    /** Retrieves a single movie by its unique ID. */
    override suspend fun getMovieById(id: Int): Movie? {
        val movie = movieDao.getMovieById(id)?.toDomain()
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched movie by id=$id: ${movie?.title ?: "Not found"}.")
        return movie
    }

    /** Updates the wishlist status of a movie by its ID. */
    override suspend fun updateWishlistStatus(id: Int, status: Boolean) {
        MyImdbLogger.d("MovieRepositoryImpl", "Updating wishlist status for movie id=$id to $status.")
        movieDao.updateWishlistStatus(id, status)
    }

    /** Retrieves all movies marked as in the wishlist. */
    override suspend fun getWishlist(): List<Movie> {
        val wishlist = movieDao.getWishlist().map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched wishlist with ${wishlist.size} movies.")
        return wishlist
    }

    /** Retrieves all genres as strings from the database. */
    override suspend fun getAllGenres(): List<String> {
        val genres = genreDao.getAllGenres().map { it.toDomain() }
        MyImdbLogger.d("MovieRepositoryImpl", "Fetched ${genres.size} genres from database.")
        return genres
    }

    /**
     * Maps a [MovieItem] (network model) to [MovieEntity] (database model).
     */
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

    /**
     * Maps a [MovieEntity] (database model) to [Movie] (domain model).
     */
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

    /**
     * Maps a [GenreEntity] to its string representation.
     */
    fun GenreEntity.toDomain(): String = name
}