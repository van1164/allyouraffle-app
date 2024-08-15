package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RaffleViewModel {
    private val api = Api
    private val _raffleList = MutableStateFlow<List<RaffleResponse>>(emptyList())
    val raffleList: StateFlow<List<RaffleResponse>> = _raffleList


    fun initRaffle(isFree: Boolean){
        if(_raffleList.value.isEmpty()){
            loadRaffles(isFree)
        }
    }

    fun loadRaffles(isFree : Boolean) {
        runBlocking {
            _raffleList.value = api.getActive(isFree)
            println(_raffleList.value)
        }
    }

    fun purchase(jwt : String, id : String): Boolean {
        return api.purchase(jwt,id)
    }
}