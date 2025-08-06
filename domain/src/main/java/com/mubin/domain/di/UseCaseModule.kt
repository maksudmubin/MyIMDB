package com.mubin.domain.di

import com.mubin.domain.repo.MovieRepository
import com.mubin.domain.usecase.GetGenres
import com.mubin.domain.usecase.GetPaginatedMovies
import com.mubin.domain.usecase.GetPaginatedMoviesByGenre
import com.mubin.domain.usecase.InitializeMovies
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideInitializeMovies(
        repo: MovieRepository
    ): InitializeMovies = InitializeMovies(repo)

    @Provides
    @Singleton
    fun provideGetPaginatedMovies(
        repo: MovieRepository
    ): GetPaginatedMovies = GetPaginatedMovies(repo)

    @Provides
    @Singleton
    fun provideGetPaginatedMoviesByGenre(
        repo: MovieRepository
    ): GetPaginatedMoviesByGenre = GetPaginatedMoviesByGenre(repo)

    @Provides
    @Singleton
    fun provideGetGenres(
        repo: MovieRepository
    ): GetGenres = GetGenres(repo)

}