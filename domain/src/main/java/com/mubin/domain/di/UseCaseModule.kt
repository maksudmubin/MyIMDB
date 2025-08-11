package com.mubin.domain.di

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.repo.MovieRepository
import com.mubin.domain.usecase.GetAllGenresUseCase
import com.mubin.domain.usecase.GetMovieByIdUseCase
import com.mubin.domain.usecase.GetMoviesByGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryAndGenrePaginatedUseCase
import com.mubin.domain.usecase.GetMoviesByQueryPaginatedUseCase
import com.mubin.domain.usecase.GetMoviesPaginatedUseCase
import com.mubin.domain.usecase.GetTotalMovieCountUseCase
import com.mubin.domain.usecase.GetTotalWishlistCountUseCase
import com.mubin.domain.usecase.GetWishlistUseCase
import com.mubin.domain.usecase.IsMovieInWishlistUseCase
import com.mubin.domain.usecase.SyncMoviesIfNeededUseCase
import com.mubin.domain.usecase.UpdateWishlistStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides all use case instances for dependency injection.
 *
 * This module supplies singletons of each use case by injecting
 * the required [MovieRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideSyncMoviesIfNeededUseCase(repository: MovieRepository): SyncMoviesIfNeededUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing SyncMoviesIfNeededUseCase")
        return SyncMoviesIfNeededUseCase(repository)
    }

    @Provides
    fun provideGetTotalMovieCountUseCase(repository: MovieRepository): GetTotalMovieCountUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetTotalMovieCountUseCase")
        return GetTotalMovieCountUseCase(repository)
    }

    @Provides
    fun provideGetMoviesPaginatedUseCase(repository: MovieRepository): GetMoviesPaginatedUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetMoviesPaginatedUseCase")
        return GetMoviesPaginatedUseCase(repository)
    }

    @Provides
    fun provideGetMoviesByGenrePaginatedUseCase(repository: MovieRepository): GetMoviesByGenrePaginatedUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetMoviesByGenrePaginatedUseCase")
        return GetMoviesByGenrePaginatedUseCase(repository)
    }

    @Provides
    fun provideGetMoviesByQueryPaginatedUseCase(repository: MovieRepository): GetMoviesByQueryPaginatedUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetMoviesByQueryPaginatedUseCase")
        return GetMoviesByQueryPaginatedUseCase(repository)
    }

    @Provides
    fun provideGetMoviesByQueryAndGenrePaginatedUseCase(repository: MovieRepository): GetMoviesByQueryAndGenrePaginatedUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetMoviesByQueryAndGenrePaginatedUseCase")
        return GetMoviesByQueryAndGenrePaginatedUseCase(repository)
    }

    @Provides
    fun provideGetMovieByIdUseCase(repository: MovieRepository): GetMovieByIdUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetMovieByIdUseCase")
        return GetMovieByIdUseCase(repository)
    }

    @Provides
    fun provideUpdateWishlistStatusUseCase(repository: MovieRepository): UpdateWishlistStatusUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing UpdateWishlistStatusUseCase")
        return UpdateWishlistStatusUseCase(repository)
    }

    @Provides
    fun provideGetWishlistUseCase(repository: MovieRepository): GetWishlistUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetWishlistUseCase")
        return GetWishlistUseCase(repository)
    }

    @Provides
    fun provideIsMovieInWishlistUseCase(repository: MovieRepository): IsMovieInWishlistUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing IsMovieInWishlistUseCase")
        return IsMovieInWishlistUseCase(repository)
    }

    @Provides
    fun provideGetTotalWishlistCountUseCase(repository: MovieRepository): GetTotalWishlistCountUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetTotalWishlistCountUseCase")
        return GetTotalWishlistCountUseCase(repository)
    }

    @Provides
    fun provideGetAllGenresUseCase(repository: MovieRepository): GetAllGenresUseCase {
        MyImdbLogger.d("UseCaseModule", "Providing GetAllGenresUseCase")
        return GetAllGenresUseCase(repository)
    }
}