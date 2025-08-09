package com.mubin.network.service

import com.mubin.network.model.MoviesResponse
import retrofit2.http.GET

/**
 * Retrofit API service interface for fetching movie data from the remote source.
 *
 * This service defines endpoints used in the MyIMDB app to retrieve movie information.
 */
interface MovieApiService {

    /**
     * Fetches the full list of movies from the remote JSON endpoint.
     *
     * The endpoint returns a [MoviesResponse] object containing movie data.
     *
     * @return A suspendable [MoviesResponse] containing the movies.
     */
    @GET("movies-list/master/db.json")
    suspend fun fetchMovies(): MoviesResponse
}