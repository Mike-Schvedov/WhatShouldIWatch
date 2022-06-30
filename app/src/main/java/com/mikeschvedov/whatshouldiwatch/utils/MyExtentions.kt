package com.mikeschvedov.whatshouldiwatch.utils


import kotlinx.coroutines.flow.flow

fun <T> Any.toFlow(content :T) = flow{
    emit(content)
}