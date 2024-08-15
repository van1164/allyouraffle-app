package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RaffleViewModel {
    private val api = Api
    private val _raffleList = MutableStateFlow<List<RaffleResponse>>(emptyList())
    val raffleList: StateFlow<List<RaffleResponse>> = _raffleList


    fun loadRaffles() {
        GlobalScope.launch {
            _raffleList.value = api.getActive()
            println(_raffleList.value)
        }
        println("XXXXXXXXXXXXXXXXXXXX")
    }

    fun purchase(jwt : String, id : String): Boolean {
        return api.purchase(jwt,id)
    }
}