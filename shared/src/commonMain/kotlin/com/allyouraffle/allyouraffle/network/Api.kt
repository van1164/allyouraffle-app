package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://allyouraffle.co.kr/"

val ktorClient = HttpClient(CIO){
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

object Api {
    suspend fun getActive(): List<RaffleResponse> {
        val response = ktorClient
            .get(BASE_URL + "api/v1/raffle/active")

        if(response.status != HttpStatusCode.OK){
            throw NetworkException()
        }

        return response.body<List<RaffleResponse>>()
    }
}
