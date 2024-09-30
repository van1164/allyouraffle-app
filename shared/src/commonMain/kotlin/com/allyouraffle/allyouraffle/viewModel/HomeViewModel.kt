package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.RaffleApi
import com.allyouraffle.allyouraffle.network.getTickets
import com.allyouraffle.allyouraffle.network.ticketPlus
import com.allyouraffle.allyouraffle.util.CommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class HomeViewModel : BaseViewModel() {
    private val _ticketCount = MutableStateFlow(-1)
    val ticketCount = _ticketCount.asStateFlow()

    private val _popularRaffleList = MutableStateFlow<List<RaffleResponse>>(emptyList())
    val popularRaffleList = _popularRaffleList.asStateFlow()

    suspend fun ticketPlusOne(jwt: String) {
        safeApiCall {
            _ticketCount.update { ticketPlus(jwt) }
        }
    }

    suspend fun initHomeV2(jwt: String) {
        safeApiCall {
            if (_popularRaffleList.value.isEmpty()) {
                _loadPopularRaffleList()
            }
        }
    }

    @Deprecated("이제 티켓이랑 분리함.")
    suspend fun initHome(jwt: String) {
        safeApiCall {
            if (_popularRaffleList.value.isEmpty()) {
                _loadPopularRaffleList()
            }
            _loadTickets(jwt)
        }
    }

    @Deprecated("이제 티켓이랑 분리함.")
    suspend fun refresh(jwt: String) {
        safeApiCall {
            _loadPopularRaffleList()
            _loadTickets(jwt)
        }
    }

    suspend fun refreshV2() {
        safeApiCall {
            _loadPopularRaffleList()
        }
    }

    suspend fun loadTickets(jwt: String) {
        safeApiCall {
            _loadTickets(jwt)
        }
    }

    private suspend fun _loadTickets(jwt: String) {
        _ticketCount.update { getTickets(jwt) }
    }

    suspend fun loadPopularRaffleList() {
        safeApiCall {
            _loadPopularRaffleList()
        }
    }

    private suspend fun _loadPopularRaffleList(){
        _popularRaffleList.update {
            RaffleApi.loadPopularRaffleList()
        }
    }
    fun <T> StateFlow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)
    fun printTest() {
        println("BBBBBBBBBBBBBBBB")
    }
}