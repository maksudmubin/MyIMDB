package com.mubin.network.model

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    @SerializedName("genres")
    val genres: List<String> = emptyList(),

    @SerializedName("movies")
    val movies: List<MovieItem> = emptyList()
)
