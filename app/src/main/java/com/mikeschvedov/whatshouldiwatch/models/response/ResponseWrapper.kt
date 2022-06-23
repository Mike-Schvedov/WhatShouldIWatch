package com.mikeschvedov.whatshouldiwatch.models.response

import com.google.gson.annotations.SerializedName

// Movie AND TV SHOW
class ItemWrapper<T : TmdbItem>(
    @SerializedName("results")
    val items: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val pages: Int
)

class VideoWrapper(
    @SerializedName("results")
    val videos: List<Video>
)

class CreditWrapper(
    val cast: List<Cast>,
    val crew: List<Crew>
)