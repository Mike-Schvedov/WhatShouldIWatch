package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem

fun interface OnClickItemListener {
    fun onItemClicked(
        item: TmdbItem
    )
}