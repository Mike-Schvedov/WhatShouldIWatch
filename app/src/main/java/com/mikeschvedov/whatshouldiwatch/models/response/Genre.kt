package com.mikeschvedov.whatshouldiwatch.models.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Genre(
    @PrimaryKey
    @SerializedName("id")
    val genreId: Long,
    val name: String
)
