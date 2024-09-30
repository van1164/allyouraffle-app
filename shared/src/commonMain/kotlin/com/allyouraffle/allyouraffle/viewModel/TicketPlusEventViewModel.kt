package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.ticketPlus
import com.allyouraffle.allyouraffle.network.ticketPlusWithReward
import kotlinx.coroutines.flow.update

class TicketPlusEventViewModel : BaseViewModel() {
    suspend fun ticketPlusMany(jwt: String,reward : Int) {
        safeApiCall {
            ticketPlusWithReward(jwt,reward)
        }
    }
}