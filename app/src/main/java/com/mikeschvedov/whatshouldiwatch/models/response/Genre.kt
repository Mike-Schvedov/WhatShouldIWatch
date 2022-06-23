package com.mikeschvedov.whatshouldiwatch.models.response

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Genre(
    @PrimaryKey
    @SerializedName("id")
    val genreId: Long,
    val name: String
)
