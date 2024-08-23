package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.exception.PurchaseException
import com.allyouraffle.allyouraffle.exception.RaffleNotFoundException
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.network.RaffleApi
import com.allyouraffle.allyouraffle.network.getTickets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RaffleDetailViewModel : BaseViewModel() {
    private val api = RaffleApi
    private val _raffleDetail = MutableStateFlow<RaffleDetailResponse?>(null)
    val raffleDetail: StateFlow<RaffleDetailResponse?> = _raffleDetail.asStateFlow()

    private val _raffleEnd = MutableStateFlow<Boolean>(false)
    val raffleEnd = _raffleEnd.asStateFlow()

    private val _purchaseSuccess = MutableStateFlow(false)
    val purchaseSuccess = _purchaseSuccess.asStateFlow()

    private val _purchaseFail = MutableStateFlow(false)
    val purchaseFail = _purchaseFail.asStateFlow()

    private val _userTickets = MutableStateFlow<Int?>(null)
    val userTickets = _userTickets.asStateFlow()

    suspend fun initRaffleDetail(jwt: String,id: String,isFree: Boolean){
        safeApiCall {
            try {
                _raffleDetail.update {
                    api.getDetail(id, isFree)
                }
            } catch (e: RaffleNotFoundException) {
                _raffleEnd.update { true }
            }
            _userTickets.update {
                getTickets(jwt)
            }
        }
    }

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
    }

    suspend fun purchase(jwt: String, id: String) {
        safeApiCall {
            try {
                if (api.purchase(jwt, id)) {
                    _purchaseSuccess.update { true }
                }
            } catch (e: PurchaseException) {
                _error.update { e.message }
                _purchaseFail.update { true }
            }

        }
    }

    suspend fun purchaseWithTicket(jwt: String, id: String) {
        safeApiCall {
            try {
                if (api.purchaseWithTicket(jwt, id)) {
                    _purchaseSuccess.update { true }
                }
            } catch (e: PurchaseException) {
                _error.update { e.message }
                _purchaseFail.update { true }
            }
        }
    }

    fun setSuccessFalse() {
        _purchaseSuccess.update { false }
    }

    suspend fun loadUserTickets(jwt: String) {
        safeApiCall {
            _userTickets.update {
                getTickets(jwt)
            }
        }
    }

    fun setFailFalse() {
        _purchaseFail.update { false }
    }
}