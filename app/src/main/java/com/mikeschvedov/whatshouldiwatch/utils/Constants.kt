package com.mikeschvedov.whatshouldiwatch.utils


class Constants {
    companion object {

        const val IMAGE_LOCATION = "https://image.tmdb.org/t/p/w500"

        const val NO_INTERNET_EXCEPTION = "It seems there is no internet connection."
        const val API_EXCEPTION = "There is a problem with the server."
        const val UNKNOWN_EXCEPTION = "Something went wrong, could not fetch the data."

        const val QUERY_PAGE_SIZE = 20


    }
}

object CATEGORY{
    val POPULAR_MOVIES = 1L to "Popular Movies"
    val TOP_RATED_MOVIES = 2L to "Top Rated Movies"
    val NEW_RELEASED_MOVIES = 3L to "New Releases"
    val NOW_PLAYING_MOVIES = 4L to "Now Playing"

    val POPULAR_SHOWS = 5L to "Popular Tv Shows"
    val TOP_RATED_SHOWS = 6L to "Top Rated Tv Shows"
    val NEW_RELEASED_SHOWS = 7L to "New Tv Shows"
}
