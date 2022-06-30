package com.mikeschvedov.whatshouldiwatch.utils

import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem

class ItemWrapper(val item: TmdbItem? = null)

class ItemListWrapper(val itemList: List<TmdbItem>? = null)