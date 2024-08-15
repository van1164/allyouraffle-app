package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
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
    suspend fun getActive(isFree: Boolean): List<RaffleResponse> {
        val response : HttpResponse = if(isFree){
            ktorClient
                .get(BASE_URL + "api/v1/raffle/active/free")
        } else{
            ktorClient
                .get(BASE_URL + "api/v1/raffle/active/not_free")
        }

        checkNotOkThrowNetworkException(response.status)

        return response.body<List<RaffleResponse>>()
    }

    fun getDetail(id: String, isFree : Boolean): RaffleDetailResponse {
        return if(isFree){
            getDetailFree(id)
        }else{
            getDetailNotFree(id)
        }
    }

    private fun getDetailFree(id : String) : RaffleDetailResponse {
        return runBlocking {
            val response = ktorClient
                .get(BASE_URL + "api/v1/raffle/active/free/detail/"+id)

            checkNotOkThrowNetworkException(response.status)

            return@runBlocking response.body<RaffleDetailResponse>()
        }
    }

    private fun getDetailNotFree(id : String) : RaffleDetailResponse {
        return runBlocking {
            val response = ktorClient
                .get(BASE_URL + "api/v1/raffle/active/not_free/detail/"+id)

            checkNotOkThrowNetworkException(response.status)

            return@runBlocking response.body<RaffleDetailResponse>()
        }
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

    enum class ApiState(val message : String) {
        Before("실행 전"),
        Loading("로딩중"),
        Error("실패"),
        Success("성공")
    }


}

fun checkNotOkThrowNetworkException(statusCode: HttpStatusCode, message: String? = null) {
    if (statusCode != HttpStatusCode.OK) {
        throw message?.let { NetworkException(it) } ?: NetworkException()
    }
}