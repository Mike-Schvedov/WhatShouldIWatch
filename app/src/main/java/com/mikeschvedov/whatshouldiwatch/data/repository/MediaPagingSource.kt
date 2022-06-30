package com.mikeschvedov.whatshouldiwatch.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.models.response.Success
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

const val STARTING_PAGE_INDEX = 1
const val NETWORK_PAGE_SIZE = 10

class MediaPagingSource @Inject constructor(
    private val remoteApi: RemoteApi,
  //  private val query: String,
   // private val sort: String?,
) :
    PagingSource<Int, TmdbItem>() {

    //Implementations of PagingSource must define how refreshes resume from the middle of the loaded paged data.
    // Do this by implementing getRefreshKey()
    // to map the correct initial key using state.anchorPosition as the most recently accessed index.
    // We need to get the previous key (or next key if previous is null) of the page
    // that was closest to the most recently accessed index.
    // Anchor position is the most recently accessed index.
    override fun getRefreshKey(state: PagingState<Int, TmdbItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TmdbItem> {
        val position = params.key ?: STARTING_PAGE_INDEX
       // val apiQuery = query

        //search     @GET("v2/everything")

        return try {
            val response =
                remoteApi.topRatedMovies(position)

            lateinit var mediaItems: List<TmdbItem>
            if (response is Success) {
                mediaItems = response.data.items
            }

            val nextKey = if (mediaItems.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // after that load size = NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicate items, at the 2nd request

                if (params.loadSize == 3 * NETWORK_PAGE_SIZE) {
                    position + 1
                } else {
                    position + (params.loadSize / NETWORK_PAGE_SIZE)
                }
            }
            LoadResult.Page(
                data = mediaItems,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}