package com.allyouraffle.allyouraffle.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

class CommonFlow<T>(private val origin: StateFlow<T>) {
    fun subscribe(block: (T) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            origin.collect { block(it) }
        }
    }
}