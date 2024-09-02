package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.exception.JwtException
import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.exception.PurchaseException
import com.allyouraffle.allyouraffle.exception.RaffleNotFoundException
import com.allyouraffle.allyouraffle.model.RaffleDetailResponse
import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BASE_URL = "https://api.allyouraffle.co.kr/"

val ktorClient = HttpClient() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

object RaffleApi {

    suspend fun getRaffleHistoryList(jwt: String,offset:Long,size:Long): List<PurchaseHistory> {
        val response = ktorClient
            .get(BASE_URL + "api/v1/purchase_history") {
                parameter("offset",offset)
                parameter("size",size)
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $jwt")
            }
        checkJwtExpire(response.status)
        checkNotOkThrowNetworkException(response.status)
        return response.body()

    }

    suspend fun getActive(isFree: Boolean): List<RaffleResponse> {
        val response: HttpResponse = if (isFree) {
            ktorClient
                .get(BASE_URL + "api/v1/raffle/active/free")
        } else {
            ktorClient
                .get(BASE_URL + "api/v1/raffle/active/not_free")
        }

        checkNotOkThrowNetworkException(response.status)
        return response.body<List<RaffleResponse>>()
    }

    suspend fun getDetail(id: String, isFree: Boolean): RaffleDetailResponse {
        return if (isFree) {
            getDetailFree(id)
        } else {
            getDetailNotFree(id)
        }
    }

    private suspend fun getDetailFree(id: String): RaffleDetailResponse {
        val response = ktorClient
            .get(BASE_URL + "api/v1/raffle/active/free/detail/" + id)
        println(response)
        if (response.status == HttpStatusCode.NotFound) throw RaffleNotFoundException("래플 마감.")
        checkNotOkThrowNetworkException(response.status)

        return response.body<RaffleDetailResponse>()
    }

    private fun getDetailNotFree(id: String): RaffleDetailResponse {
        return runBlocking {
            val response = ktorClient
                .get(BASE_URL + "api/v1/raffle/active/not_free/detail/" + id)

            checkNotOkThrowNetworkException(response.status)

            return@runBlocking response.body<RaffleDetailResponse>()
        }
    }

    suspend fun purchase(jwt: String, id: String): Boolean {
        val response = ktorClient
            .post(BASE_URL + "api/v1/raffle/purchase/" + id) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $jwt")
            }

        if (response.status == HttpStatusCode.BadRequest) throw (response.body() as ErrorResponse).message?.let {
            PurchaseException(
                it
            )
        } ?: PurchaseException("구매에 실패하였습니다.")
        checkJwtExpire(response.status)
        checkNotOkThrowNetworkException(response.status)
        return true

    }

    suspend fun purchaseWithTicket(jwt: String, id: String): Boolean {
        val response = ktorClient
            .post(BASE_URL + "api/v1/raffle/purchase_ticket_one/" + id) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $jwt")
            }
        println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP")
        println(response)
        if(response.status != HttpStatusCode.OK){
            val body : ErrorResponse? = response.body() as? ErrorResponse
            println(body)
            if (body != null) throw PurchaseException(body.message.toString())
        }
        checkJwtExpire(response.status)
        checkNotOkThrowNetworkException(response.status)
        return true

    }

    suspend fun loadPopularRaffleList(): List<RaffleResponse> {
        val response = ktorClient.get(BASE_URL + "api/v1/raffle/active/popular") {
            contentType(ContentType.Application.Json)
        }
        checkNotOkThrowNetworkException(response.status)
        return response.body()
    }

}


fun checkNotOkThrowNetworkException(statusCode: HttpStatusCode, message: String? = null) {
    if (statusCode != HttpStatusCode.OK) {
        throw message?.let { NetworkException(it) } ?: NetworkException()
    }
}

fun checkJwtExpire(statusCode: HttpStatusCode) {
    if (statusCode == HttpStatusCode.Forbidden) {
        throw JwtException()
    }
}

@Serializable
data class ErrorResponse(
    val message: String?,
    val description: String?
)
