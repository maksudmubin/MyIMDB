package com.mubin.database.di

import android.content.Context
import androidx.room.Room
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.database.dao.GenreDao
import com.mubin.database.dao.MovieDao
import com.mubin.database.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing Room database and DAO dependencies in the MyIMDB app.
 *
 * This module ensures that:
 * - A single instance of [AppDatabase] is provided across the app lifecycle.
 * - DAOs ([MovieDao] and [GenreDao]) are injected wherever needed.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides an instance of [MovieDao] from the given [AppDatabase].
     */
    @Provides
    fun provideMovieDao(db: AppDatabase): MovieDao {
        MyImdbLogger.d("DatabaseModule", "Providing MovieDao instance.")
        return db.movieDao()
    }

    /**
     * Provides an instance of [GenreDao] from the given [AppDatabase].
     */
    @Provides
    fun provideGenreDao(db: AppDatabase): GenreDao {
        MyImdbLogger.d("DatabaseModule", "Providing GenreDao instance.")
        return db.genreDao()
    }

    /**
     * Creates and provides the [AppDatabase] instance.
     *
     * @param context Application context for building the database.
     * @return A configured [AppDatabase] instance.
     */
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        MyImdbLogger.d("DatabaseModule", "Creating AppDatabase instance.")
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_imdb_db"
        )
            .fallbackToDestructiveMigration(false) // Prevents destructive migration unless explicitly allowed
            .build()
    }
}