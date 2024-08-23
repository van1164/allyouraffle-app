package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.RaffleApi
import com.allyouraffle.allyouraffle.network.getTickets
import com.allyouraffle.allyouraffle.network.ticketPlus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    suspend fun initHome(jwt : String){
        if(_popularRaffleList.value.isEmpty()){
            loadPopularRaffleList()
        }
        loadTickets(jwt)
    }

    suspend fun refresh(jwt: String){
        loadPopularRaffleList()
        loadTickets(jwt)
    }

    suspend fun loadTickets(jwt: String) {
        safeApiCall {
            _ticketCount.update { getTickets(jwt) }
        }
    }

    suspend fun loadPopularRaffleList(){
        safeApiCall {
            _popularRaffleList.update {
                RaffleApi.loadPopularRaffleList()
            }
        }
    }
}