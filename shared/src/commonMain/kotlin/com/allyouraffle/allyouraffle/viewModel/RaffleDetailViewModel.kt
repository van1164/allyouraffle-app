package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.network.Api
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RaffleDetailViewModel : AbstractViewModel() {
    private val api = Api
    private val _raffleDetail = MutableStateFlow<RaffleDetailResponse?>(null)
    val raffleDetail: StateFlow<RaffleDetailResponse?> = _raffleDetail.asStateFlow()


    fun getDetail(id : String, isFree : Boolean) {
        _apiState.update {
            Api.ApiState.Loading
        }
        _raffleDetail.update {
            api.getDetail(id,isFree)
        }
        _apiState.update {
            Api.ApiState.Success
        }
    }

    fun purchase(jwt : String, id : String): Boolean {
        return api.purchase(jwt,id)
    }
}