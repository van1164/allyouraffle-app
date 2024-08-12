package com.allyouraffle.allyouraffle.model

import kotlinx.serialization.Serializable

@Serializable
data class RaffleResponse(
    val totalCount: Int,
    val currentCount: Int,
    val ticketPrice: Int,
    val status: String,
    val item: Item
) {
    val progress: Float get() = (currentCount.toFloat() / totalCount.toFloat()) * 100
}
@Serializable
data class Item(
    val name: String,
    val imageUrl: String,
    val description: String
)