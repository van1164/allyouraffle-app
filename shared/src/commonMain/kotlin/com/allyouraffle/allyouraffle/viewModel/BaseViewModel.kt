package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.util.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel {
    protected val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    val loadingForIos = _loading.asCommonFlow()


    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    val errorForIos = _error.asCommonFlow()

    protected suspend fun safeApiCall(apiCall: suspend () -> Unit): Unit {
        _loading.update { true }
        try {
            apiCall()
        } catch (e: NetworkException) {
            println(e)
            _error.update { "네트워크에러가 발생했습니다." }
        } catch (e: Exception) {
            println(e)
            _error.update { "예기치 못한 오류가 발생했습니다." }
        } finally {
            _loading.update { false }
        }
    }

    fun setNullError() {
        _error.update {
            null
        }
    }

    fun setLoadingFalse(){
        _loading.update {
            false
        }
    }

    fun setLoadingTrue() {
        _loading.update {
            true
        }
    }

    fun setError(message : String) {
        _error.update { message }
    }
}
