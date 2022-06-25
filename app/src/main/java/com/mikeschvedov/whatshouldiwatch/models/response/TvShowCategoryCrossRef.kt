package com.mikeschvedov.whatshouldiwatch.models.response

import androidx.room.Entity

@Entity(primaryKeys = ["id", "categoryId"])
data class TvShowCategoryCrossRef(
    //@PrimaryKey(autoGenerate = true)
    //val id: Long,
    val id: Long,
    val categoryId: Long
)