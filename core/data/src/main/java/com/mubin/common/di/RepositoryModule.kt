package com.mubin.common.di

import com.mubin.common.repo.MovieRepositoryImpl
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.domain.repo.MovieRepository
import com.mubin.network.service.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideMovieRepository(
        movieApi: MovieApiService,
        movieDao: MovieDao,
        genreDao: GenreDao
    ): MovieRepository {
        return MovieRepositoryImpl(movieApi, movieDao, genreDao)
    }

}