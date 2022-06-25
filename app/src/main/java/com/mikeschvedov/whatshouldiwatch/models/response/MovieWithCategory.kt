package com.mikeschvedov.whatshouldiwatch.models.response

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieWithCategory(
    @Embedded
    val movie: Movie,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId",
        associateBy = Junction(MovieCategoryCrossRef::class)
    )
    val categories: List<Category>
)
