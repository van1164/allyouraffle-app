package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.exception.RaffleNotFoundException
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RaffleDetailViewModel : BaseViewModel() {
    private val api = Api
    private val _raffleDetail = MutableStateFlow<RaffleDetailResponse?>(null)
    val raffleDetail: StateFlow<RaffleDetailResponse?> = _raffleDetail.asStateFlow()

    private val _raffleEnd = MutableStateFlow<Boolean>(false)
    val raffleEnd = _raffleEnd.asStateFlow()

    private val _purchaseSuccess = MutableStateFlow(false)
    val purchaseSuccess = _purchaseSuccess.asStateFlow()

    private val _purchaseFail = MutableStateFlow(false)
    val purchaseFail = _purchaseFail.asStateFlow()

    suspend fun getDetail(id: String, isFree: Boolean) {
        safeApiCall {
            try {
                _raffleDetail.update {
                    api.getDetail(id, isFree)
                }
            } catch (e: RaffleNotFoundException) {
                _raffleEnd.update { true }
            }
        }
        println("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK")
    }

    suspend fun purchase(jwt: String, id: String) {
        safeApiCall {
            if (api.purchase(jwt, id)) {
                _purchaseSuccess.update { true }
            } else {
                _purchaseFail.update { true }
            }
        }
    }

    fun setSuccessFalse() {
        _purchaseSuccess.update { false }
    }

    fun setFailFalse() {
        _purchaseFail.update { false }
    }
}