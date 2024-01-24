package com.free.movies.domain.extentions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ViewModel.scopeWithContext(
    dispatchers: CoroutineDispatcher = Dispatchers.IO,
    block: suspend CoroutineScope.() -> Unit,
) {
    viewModelScope.launch {
        withContext(dispatchers) {
            block.invoke(this)
        }
    }
}