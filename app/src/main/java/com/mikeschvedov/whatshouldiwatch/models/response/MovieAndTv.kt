package com.mikeschvedov.whatshouldiwatch.models.response

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

interface TmdbItem : Parcelable {
    val id : Long
    val overview: String
    val releaseDate: String?
    val posterPath: String?
    val backdropPath: String?
    val name: String
    val voteAverage: Double
}

@Entity
data class Category(
    @PrimaryKey
    val categoryId: Long,
    val categoryName: String
)

@Entity
@Parcelize
data class Movie(
    @PrimaryKey
    override val id: Long,
    override val overview: String,
    @SerializedName("release_date")
    override val releaseDate: String?,
    @SerializedName("poster_path")
    override val posterPath: String?,
    @SerializedName("backdrop_path")
    override val backdropPath: String?,
    @SerializedName("title")
    override var name: String,
    @SerializedName("vote_average")
    override val voteAverage: Double,
) : TmdbItem {
}


@Parcelize
@Entity
data class TVShow(
    @PrimaryKey
    override val id: Long,
    override val overview: String,
    @SerializedName("first_air_date")
    override val releaseDate: String?,
    @SerializedName("poster_path")
    override val posterPath: String?,
    @SerializedName("backdrop_path")
    override val backdropPath: String?,
    override val name: String,
    @SerializedName("vote_average")
    override val voteAverage: Double,
) : TmdbItem
