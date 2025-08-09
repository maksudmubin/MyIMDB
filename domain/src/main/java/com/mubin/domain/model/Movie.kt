package com.mubin.domain.model

import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Domain model representing a movie in the MyIMDB app.
 *
 * This class is used throughout the app's business logic and UI layers.
 *
 * @property id Unique identifier for the movie.
 * @property title Title of the movie.
 * @property year Release year of the movie as a string.
 * @property runtime Runtime duration of the movie.
 * @property genres List of genres the movie belongs to.
 * @property director Name of the movie's director.
 * @property actors Comma-separated string of main actors.
 * @property plot Short plot summary of the movie.
 * @property posterUrl URL to the movie's poster image.
 * @property isInWishlist Flag indicating if the movie is marked as in the wishlist. Defaults to false.
 */
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
) {
    init {
        MyImdbLogger.d("Movie", "Domain model created: id=$id, title=\"$title\"")
    }
}