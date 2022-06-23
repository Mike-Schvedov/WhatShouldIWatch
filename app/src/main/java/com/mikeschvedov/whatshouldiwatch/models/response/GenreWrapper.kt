package com.mikeschvedov.whatshouldiwatch.models.response

import com.google.gson.annotations.SerializedName

class GenreWrapper(
    val genres: List<Genre>,
    @SerializedName("status_message")
    val statusMessage: String?,
    @SerializedName("status_code")
    val statusCode: Int?,
    val success: Boolean?
)