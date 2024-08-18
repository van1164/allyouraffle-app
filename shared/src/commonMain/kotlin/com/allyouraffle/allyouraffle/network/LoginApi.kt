package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.exception.NetworkException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable


@Serializable
data class MobileUserLoginDto(
    val registrationId : String,
    val email : String,
    val name : String,
    val profileImageUrl : String?,
    val userNameAttributeNameValue : String
)
@Serializable
data class MobileLoginResponse(
    val jwt : String,
    val refreshToken : String
)

@Serializable
data class JwtTokenResponse(
    val jwt : String,
)


object LoginApi {
    fun login(mobileUserLoginDto: MobileUserLoginDto): MobileLoginResponse {
        return runBlocking {
            val response = ktorClient
                .post(BASE_URL + "api/v1/login"){
                    contentType(ContentType.Application.Json)
                    setBody(mobileUserLoginDto)
                }
            println(response.status)

            if ( response.status != HttpStatusCode.OK){
                throw NetworkException()
            }

            response.body()
        }
    }

    fun verify(jwt: String): Boolean {
        return runBlocking {
            val response = ktorClient.post(BASE_URL + "api/v1/login/verify?jwt="+jwt)
            return@runBlocking response.status == HttpStatusCode.OK

        }
    }

    fun refresh(refreshToken: String) : String? {
        return runBlocking {
            val response = ktorClient.post(BASE_URL+"api/v1/login/refresh?refresh="+refreshToken)
            println(response.toString())
            if (response.status != HttpStatusCode.OK){
                return@runBlocking null
            }
            return@runBlocking response.body()
        }
    }
}
