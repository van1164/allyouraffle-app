package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.getTickets
import com.allyouraffle.allyouraffle.network.ticketPlus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicketViewModel : BaseViewModel() {
    private val _ticketCount = MutableStateFlow(-1)
    val ticketCount = _ticketCount.asStateFlow()


    suspend fun ticketPlusOne(jwt: String) {
        safeApiCall {
            _ticketCount.update { ticketPlus(jwt) }
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
}