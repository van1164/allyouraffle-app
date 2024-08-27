package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.PurchaseHistory
import com.allyouraffle.allyouraffle.network.RaffleApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RaffleHistoryViewModel : BaseViewModel() {
    private val _purchaseHistoryList = MutableStateFlow(emptyList<PurchaseHistory>())
    val purchaseHistory = _purchaseHistoryList.asStateFlow()
    private val size: Long = 10
    private val _offset = MutableStateFlow<Long>(0)
    private val _isLast = MutableStateFlow(false)

    suspend fun loadHistory(jwt: String) {
        if (!_isLast.value) {
            safeApiCall {
                val result = RaffleApi.getRaffleHistoryList(jwt, _offset.value, size)
                if (result.isEmpty()) {
                    _isLast.update { true }
                }
                _purchaseHistoryList.update { currentItem ->
                    currentItem + result
                }
                _offset.update { _offset.value + size }
            }
        }
    }

    suspend fun initHistory(jwt: String) {
        safeApiCall {
            _offset.update { 0 }
            _isLast.update { false }
            val result = RaffleApi.getRaffleHistoryList(jwt, _offset.value, size)
            if (result.isEmpty()) {
                _isLast.update { true }
            }
            _purchaseHistoryList.update { result }
            _offset.update { cur ->
                cur + size
            }
        }
    }
}