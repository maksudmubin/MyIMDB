package com.mubin.network.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.mubin.network.service.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://raw.githubusercontent.com/erik-sytnyk/"

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        val cacheSize = 5L * 1024L * 1024L // 5MB
        val cache = Cache(
            directory = File(context.cacheDir, "${context.packageName}.cache"),
            maxSize = cacheSize
        )

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            cache(cache)
            addInterceptor(logging)
            addInterceptor(OkHttpProfilerInterceptor())
        }.build()

    }

    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)
}