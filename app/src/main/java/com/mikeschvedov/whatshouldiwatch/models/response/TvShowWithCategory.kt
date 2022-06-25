package com.mikeschvedov.whatshouldiwatch.models.response

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TvShowWithCategory(
    @Embedded
    val tvShow: TVShow,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId",
        associateBy = Junction(TvShowCategoryCrossRef::class)
    )
    val categories: List<Category>
)
