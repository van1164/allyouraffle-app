package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.RaffleApi
import com.allyouraffle.allyouraffle.network.getTickets
import com.allyouraffle.allyouraffle.util.CommonFlow
import com.allyouraffle.allyouraffle.util.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RaffleViewModel : BaseViewModel() {
    private val api = RaffleApi
    private val _raffleList = MutableStateFlow<List<RaffleResponse>>(emptyList())
    val raffleList: StateFlow<List<RaffleResponse>> = _raffleList.asStateFlow()
    private val _ticketCount = MutableStateFlow(-1)
    val ticketCount = _ticketCount.asStateFlow()

    suspend fun loadTickets(jwt: String) {
        safeApiCall {
            _ticketCount.update { getTickets(jwt) }
        }
    }

    suspend fun initRaffle(isFree: Boolean) {
        println(_raffleList.value)
        if (_raffleList.value.isEmpty()) {
            loadRaffles(isFree)
        }
    }

    suspend fun loadRaffles(isFree: Boolean) {
        safeApiCall {
            _raffleList.value = api.getActive(isFree)
        }
    }
}