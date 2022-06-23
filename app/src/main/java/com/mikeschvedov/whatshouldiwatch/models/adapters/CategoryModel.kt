package com.mikeschvedov.whatshouldiwatch.models.adapters

import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem

class CategoryModel(val title: String, val childModelList: List<TmdbItem>) {
}