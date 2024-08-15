package com.allyouraffle.allyouraffle.model

import kotlinx.serialization.Serializable

@Serializable
data class RaffleDetailResponse(
    val id : Long,
    val totalCount: Int,
    val currentCount: Int,
    val ticketPrice: Int,
    val status: String,
    val item: Item
) {
    val progress: Float get() = (currentCount.toFloat() / totalCount.toFloat()) * 100
}