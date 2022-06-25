package com.mikeschvedov.whatshouldiwatch.data.remote.networking

import com.mikeschvedov.whatshouldiwatch.models.response.*
import javax.inject.Inject

class RemoteApiImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val tvApi: TvApi,
    private val personApi: PersonApi
) : RemoteApi {

    override suspend fun nowPlayingMovies(page: Int): Result<ItemWrapper<Movie>> = try {
        Success(movieApi.nowPlayingItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun popularMovies(page: Int): Result<ItemWrapper<Movie>> = try {
        Success(movieApi.popularItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun genres(): Result<GenreWrapper> = try {
        Success(movieApi.genres())
    } catch (e: Exception) {
        Failure(e)
    }


    override suspend fun topRatedMovies(page: Int): Result<ItemWrapper<Movie>> = try {
        Success(movieApi.topRatedItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun latestMovies(page: Int): Result<ItemWrapper<Movie>> = try {
        Success(movieApi.latestItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun searchMovies(page: Int, query: String): Result<ItemWrapper<Movie>> = try {
        Success(movieApi.searchItems(page, query))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun movieTrailers(movieId: Int): Result<VideoWrapper> = try {
        Success(movieApi.movieTrailers(movieId))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun movieCredits(movieId: Int): Result<CreditWrapper> = try {
        Success(movieApi.movieCredit(movieId))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun popularTvShows(page: Int): Result<ItemWrapper<TVShow>> = try {
        Success(tvApi.popularItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun topRatedTvShows(page: Int): Result<ItemWrapper<TVShow>> = try {
        Success(tvApi.topRatedItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun latestTvShows(page: Int): Result<ItemWrapper<TVShow>> = try {
        Success(tvApi.latestItems(page))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun searchTvShows(page: Int, query: String): Result<ItemWrapper<TVShow>> =
        try {
            Success(tvApi.searchItems(page, query))
        } catch (e: Exception) {
            Failure(e)
        }

    override suspend fun tvShowTrailers(tvId: Int): Result<VideoWrapper> = try {
        Success(tvApi.tvTrailers(tvId))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun tvShowCredits(tvId: Int): Result<CreditWrapper> = try {
        Success(tvApi.tvCredit(tvId))
    } catch (e: Exception) {
        Failure(e)
    }

    override suspend fun getPerson(personId: Int): Result<Person> = try {
        Success(personApi.getPerson(personId))
    } catch (e: Exception) {
        Failure(e)
    }
}