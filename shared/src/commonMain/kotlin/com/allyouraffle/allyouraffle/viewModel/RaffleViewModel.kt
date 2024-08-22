package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RaffleViewModel : BaseViewModel() {
    private val api = Api
    private val _raffleList = MutableStateFlow<List<RaffleResponse>>(emptyList())
    val raffleList: StateFlow<List<RaffleResponse>> = _raffleList

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