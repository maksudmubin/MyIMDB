package com.mubin.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val year: String,
    val runtime: String,
    val genres: List<String>,
    val director: String,
    val actors: String,
    val plot: String,
    val posterUrl: String,
    val isInWishlist: Boolean = false
)
