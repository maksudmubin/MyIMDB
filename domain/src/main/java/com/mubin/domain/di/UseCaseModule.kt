package com.mubin.domain.di

import com.mubin.domain.repo.MovieRepository
import com.mubin.domain.usecase.GetAllGenresUseCase
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.GetMoviesByGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesPaginatedUseCase
import com.mubin.domain.usecase.GetTotalMovieCountUseCase
import com.mubin.domain.usecase.GetWishlistUseCase
import com.mubin.domain.usecase.SyncMoviesIfNeededUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSyncMoviesIfNeededUseCase(repository: MovieRepository) =
        SyncMoviesIfNeededUseCase(repository)

    @Provides
    fun provideGetTotalMovieCountUseCase(repository: MovieRepository) =
        GetTotalMovieCountUseCase(repository)

    @Provides
    fun provideGetMoviesPaginatedUseCase(repository: MovieRepository) =
        GetMoviesPaginatedUseCase(repository)

    @Provides
    fun provideGetMoviesByGenrePaginatedUseCase(repository: MovieRepository) =
        GetMoviesByGenrePaginatedUseCase(repository)

    @Provides
    fun provideGetMovieByIdUseCase(repository: MovieRepository) =
        GetMovieByIdUseCase(repository)

    @Provides
    fun provideUpdateWishlistStatusUseCase(repository: MovieRepository) =
        UpdateWishlistStatusUseCase(repository)

    @Provides
    fun provideGetWishlistUseCase(repository: MovieRepository) =
        GetWishlistUseCase(repository)

    @Provides
    fun provideGetAllGenresUseCase(repository: MovieRepository) =
        GetAllGenresUseCase(repository)
}