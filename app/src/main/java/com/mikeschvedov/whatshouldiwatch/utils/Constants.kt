package com.mikeschvedov.whatshouldiwatch.utils


class Constants {
    companion object {

        const val API_KEY = "96624ea86553cd7a4caed4ecbdc35ec1"

        const val IMAGE_LOCATION = "https://image.tmdb.org/t/p/w500"

        const val NO_INTERNET_EXCEPTION = "It seems there is no internet connection."
        const val API_EXCEPTION = "There is a problem with the server."
        const val UNKNOWN_EXCEPTION = "Something went wrong, could not fetch the data."

        const val POPULAR_MOVIES = "Popular Movies"
        const val TOP_RATED_MOVIES = "Top Rated Movies"
        const val NEW_RELEASES_MOVIES = "New Releases"
        const val NOW_PLAYING_MOVIES = "Now Playing"

        const val POPULAR_SHOW = "Popular Tv Shows"
        const val TOP_RATED_SHOW = "Top Rated Tv Shows"
        const val NEW_RELEASES_SHOW = "New Tv Shows"

        const val QUERY_PAGE_SIZE = 20


    }
}
