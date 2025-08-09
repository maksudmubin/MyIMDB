package com.mubin.data.di

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.data.repo.MovieRepositoryImpl
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.domain.repo.MovieRepository
import com.mubin.network.service.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides the MovieRepository implementation for dependency injection.
 *
 * This module ensures a singleton instance of [MovieRepository] is available
 * by injecting necessary dependencies like [MovieApiService], [MovieDao], and [GenreDao].
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides an instance of [MovieRepository] using [MovieRepositoryImpl].
     *
     * @param movieApi The remote API service for movies.
     * @param movieDao The DAO for local movie data access.
     * @param genreDao The DAO for local genre data access.
     * @return A [MovieRepository] implementation.
     */
    @Provides
    fun provideMovieRepository(
        movieApi: MovieApiService,
        movieDao: MovieDao,
        genreDao: GenreDao
    ): MovieRepository {
        MyImdbLogger.d("RepositoryModule", "Providing MovieRepository instance.")
        return MovieRepositoryImpl(movieApi, movieDao, genreDao)
    }
}