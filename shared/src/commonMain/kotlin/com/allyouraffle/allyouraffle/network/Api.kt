package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

const val BASE_URL = "https://allyouraffle.co.kr/"

val ktorClient = HttpClient(CIO) {
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

        checkNotOkThrowNetworkException(response.status)

        return response.body<List<RaffleResponse>>()
    }

    fun purchase(jwt: String, id: String): Boolean {
        return runBlocking {
            val response = ktorClient
                .post(BASE_URL + "api/v1/raffle/purchase/" + id){
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $jwt")
                }
            checkNotOkThrowNetworkException(response.status)
            return@runBlocking true
        }
    }
}

fun checkNotOkThrowNetworkException(statusCode: HttpStatusCode, message: String? = null) {
    if (statusCode != HttpStatusCode.OK) {
        throw message?.let { NetworkException(it) } ?: NetworkException()
    }
}