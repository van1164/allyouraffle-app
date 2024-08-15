package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractViewModel {
    protected val _apiState = MutableStateFlow<Api.ApiState>(Api.ApiState.Before)
    val apiState = _apiState.asStateFlow()
}