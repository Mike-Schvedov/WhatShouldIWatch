package com.mikeschvedov.whatshouldiwatch.networking

import com.mikeschvedov.whatshouldiwatch.models.response.CreditWrapper
import com.mikeschvedov.whatshouldiwatch.models.response.ItemWrapper
import com.mikeschvedov.whatshouldiwatch.models.response.Movie
import com.mikeschvedov.whatshouldiwatch.models.response.VideoWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("3/movie/now_playing?language=en")
    suspend fun nowPlayingItems(@Query("page") page: Int = 1): ItemWrapper<Movie>

    @GET("3/discover/movie?language=en&sort_by=popularity.desc")
    suspend fun popularItems(@Query("page") page: Int = 1): ItemWrapper<Movie>

    @GET("3/discover/movie?vote_count.gte=500&language=en&sort_by=vote_average.desc")
    suspend fun topRatedItems(@Query("page") page: Int =1):  ItemWrapper<Movie>

    @GET("3/movie/upcoming?language=en")
    suspend fun latestItems(@Query("page") page: Int =1): ItemWrapper<Movie>

    @GET("3/search/movie?language=en")
    suspend fun searchItems(@Query("page") page: Int =1, @Query("query") query: String):  ItemWrapper<Movie>

    @GET("3/movie/{movieId}/videos")
    suspend fun movieTrailers(@Path("movieId") movieId: Int): VideoWrapper

    @GET("3/movie/{movieId}/credits")
    suspend fun movieCredit(@Path("movieId") movieId: Int): CreditWrapper

}