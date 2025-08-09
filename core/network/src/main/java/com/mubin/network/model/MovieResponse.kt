package com.mubin.network.model

import com.google.gson.annotations.SerializedName
import com.mubin.common.utils.logger.MyImdbLogger

/**
 * Represents the response structure from the movies API endpoint.
 *
 * Contains the list of available genres and the list of movies.
 *
 * @property genres List of genre names included in the response.
 * @property movies List of [MovieItem] objects representing the movies.
 */
data class MoviesResponse(

    /** List of all genres returned by the API. Defaults to an empty list. */
    @SerializedName("genres")
    val genres: List<String> = emptyList(),

    /** List of movie items returned by the API. Defaults to an empty list. */
    @SerializedName("movies")
    val movies: List<MovieItem> = emptyList()
) {
    init {
        // Log response received with counts of genres and movies for debugging
        MyImdbLogger.d(
            "MoviesResponse",
            "Received MoviesResponse with ${genres.size} genres and ${movies.size} movies"
        )
    }
}