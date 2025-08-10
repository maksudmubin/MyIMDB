package com.mubin.data.di

import com.mubin.android.di.InternetCheckModule
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.database.di.DatabaseModule
import com.mubin.domain.di.UseCaseModule
import com.mubin.network.di.NetworkModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Root Hilt module that aggregates all other dependency injection modules
 * for the MyIMDB app.
 *
 * Includes:
 * - [InternetCheckModule] for internet check dependencies
 * - [NetworkModule] for networking dependencies
 * - [DatabaseModule] for database and DAO dependencies
 * - [UseCaseModule] for business logic use cases (assumed present)
 *
 * This centralizes all DI configurations into one module installed in
 * the SingletonComponent scope.
 */
@Module(includes = [
    InternetCheckModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    UseCaseModule::class
])
@InstallIn(SingletonComponent::class)
object AppDIModule {
    init {
        MyImdbLogger.d("AppDIModule", "AppDIModule initialized with included modules.")
    }
}
