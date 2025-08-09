package com.mubin.network.model

import com.google.gson.annotations.SerializedName
import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Data class representing a single movie item fetched from the remote API.
 *
 * This class models the JSON response fields for each movie.
 *
 * @property id Unique identifier of the movie.
 * @property title Title of the movie.
 * @property year Release year as a string.
 * @property runtime Runtime duration of the movie.
 * @property genres List of genre names associated with the movie.
 * @property director Director's name.
 * @property actors Comma-separated list of main actors.
 * @property plot Short plot summary.
 * @property posterUrl URL to the movie's poster image.
 * @property isInWishlist Flag indicating if the movie is in the wishlist; defaults to false.
 */
data class MovieItem(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("runtime")
    val runtime: String,

    @SerializedName("genres")
    val genres: List<String>,

    @SerializedName("director")
    val director: String,

    @SerializedName("actors")
    val actors: String,

    @SerializedName("plot")
    val plot: String,

    @SerializedName("posterUrl")
    val posterUrl: String,

    /** Indicates if the movie is added to the wishlist; defaults to false */
    val isInWishlist: Boolean = false
) {
    init {
        // Log creation of a MovieItem instance for debugging
        MyImdbLogger.d("MovieItem", "MovieItem created: id=$id, title=\"$title\"")
    }
}
