package com.allyouraffle.allyouraffle.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


suspend fun ticketPlus(jwt: String) : Int {
    val response = ktorClient.post(BASE_URL + "api/v1/user/tickets/plus_one") {
        contentType(ContentType.Application.Json)
        headers["Authorization"] = "Bearer $jwt"
    }
    println(response)
    checkNotOkThrowNetworkException(response.status)
    return response.body()
}
suspend fun ticketPlusWithReward(jwt: String,reward: Int) : Int {
    val response = ktorClient.post(BASE_URL + "api/v1/user/tickets/plus/"+reward) {
        contentType(ContentType.Application.Json)
        headers["Authorization"] = "Bearer $jwt"
    }
    println(response)
    checkNotOkThrowNetworkException(response.status)
    return response.body()
}

suspend fun getTickets(jwt: String) : Int {
    val response = ktorClient.get(BASE_URL + "api/v1/user/tickets") {
        contentType(ContentType.Application.Json)
        headers["Authorization"] = "Bearer $jwt"
    }
    println(response)
    checkNotOkThrowNetworkException(response.status)
    return response.body()
}