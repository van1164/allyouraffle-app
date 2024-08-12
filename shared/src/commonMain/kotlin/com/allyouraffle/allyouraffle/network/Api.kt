package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://allyouraffle.co.kr/"

val ktorClient = HttpClient(CIO){
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
//    defaultRequest {
//        url {
//            protocol = URLProtocol.HTTPS
//            host = BASE_URL
//        }
//    }
//    install(ContentNegotiation) {
//        json() // for json
//    }
}

object Api {
    suspend fun getActive(): List<RaffleResponse> {
        return ktorClient
            .get(BASE_URL + "api/v1/raffle/active")
            .body()
    }
}
