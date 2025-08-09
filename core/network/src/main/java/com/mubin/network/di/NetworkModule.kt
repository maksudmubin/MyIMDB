package com.mubin.network.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.network.BuildConfig
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

/**
 * Hilt module responsible for providing networking-related dependencies
 * including Gson, OkHttpClient, Retrofit, and the Movie API service.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://raw.githubusercontent.com/erik-sytnyk/"

    /**
     * Provides a Gson instance for JSON serialization and deserialization.
     */
    @Provides
    fun provideGson(): Gson {
        MyImdbLogger.d("NetworkModule", "Providing Gson instance.")
        return GsonBuilder().create()
    }

    /**
     * Provides an OkHttpClient instance configured with:
     * - 5MB cache located in the app's cache directory
     * - Logging interceptor to log HTTP request/response bodies only if Debug Build
     * - Custom OkHttpProfilerInterceptor for profiling network calls
     *
     * @param context Application context used to set cache directory.
     */
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        MyImdbLogger.d("NetworkModule", "Building OkHttpClient with cache and logging interceptors.")

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
            if (BuildConfig.DEBUG) {
                addInterceptor(logging)
                addInterceptor(OkHttpProfilerInterceptor())
            }
        }.build()
    }

    /**
     * Provides a Retrofit instance configured with the base URL, Gson converter,
     * and the OkHttpClient.
     *
     * @param gson Gson instance for JSON parsing.
     * @param client OkHttpClient instance for network calls.
     */
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
        MyImdbLogger.d("NetworkModule", "Providing Retrofit instance.")
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    /**
     * Provides the [MovieApiService] Retrofit interface for API calls.
     *
     * @param retrofit Retrofit instance to create the service from.
     */
    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApiService {
        MyImdbLogger.d("NetworkModule", "Providing MovieApiService instance.")
        return retrofit.create(MovieApiService::class.java)
    }
}