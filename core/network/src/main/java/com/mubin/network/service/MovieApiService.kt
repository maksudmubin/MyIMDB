package com.mubin.network.service

import com.mubin.network.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.Response

interface MovieApiService {

    @GET("movies-list/master/db.json")
    suspend fun fetchMovies(): MoviesResponse
}