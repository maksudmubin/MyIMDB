package com.mubin.data.di

import com.mubin.database.di.DatabaseModule
import com.mubin.domain.di.UseCaseModule
import com.mubin.network.di.NetworkModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [
    NetworkModule::class,
    DatabaseModule::class,
    UseCaseModule::class
])
@InstallIn(SingletonComponent::class)
object AppDIModule
