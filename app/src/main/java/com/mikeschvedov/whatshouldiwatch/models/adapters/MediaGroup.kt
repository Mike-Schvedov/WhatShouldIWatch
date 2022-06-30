package com.mikeschvedov.whatshouldiwatch.models.adapters

import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem

data class MediaGroup(val title: String, val childModelList: MutableList<TmdbItem> = mutableListOf())

data class MediaGroupList(val list : MutableList<MediaGroup> = mutableListOf())