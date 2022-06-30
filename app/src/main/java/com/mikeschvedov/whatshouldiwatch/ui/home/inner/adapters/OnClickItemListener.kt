package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import kotlinx.coroutines.flow.Flow

fun interface OnClickItemListener {
    fun onItemClicked(
        item: TmdbItem
    )
}