package com.allyouraffle.allyouraffle.network

import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.exception.PhoneNumberDuplicatedException
import com.allyouraffle.allyouraffle.exception.PurchaseException
import com.allyouraffle.allyouraffle.model.RaffleResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable


object LoginApi {
    fun login(mobileUserLoginDto: MobileUserLoginDto): MobileLoginResponse {
        return runBlocking {
            val response = ktorClient
                .post(BASE_URL + "api/v1/login") {
                    contentType(ContentType.Application.Json)
                    setBody(mobileUserLoginDto)
                }
            println(response.status)

            if (response.status != HttpStatusCode.OK) {
                throw NetworkException()
            }

            response.body()
        }
    }

    fun verify(jwt: String): Boolean {
        return runBlocking {
            val response = ktorClient.post(BASE_URL + "api/v1/login/verify?jwt=" + jwt)
            return@runBlocking response.status == HttpStatusCode.OK

        }
    }

    fun refresh(refreshToken: String): JwtTokenResponse? {
        return runBlocking {
            val response =
                ktorClient.post(BASE_URL + "api/v1/login/refresh?refreshToken=" + refreshToken)
            println(response.toString())
            if (response.status != HttpStatusCode.OK) {
                return@runBlocking null
            }
            return@runBlocking response.body()
        }
    }

    suspend fun getUserInfo(jwt: String): UserInfoResponse {

        val response = ktorClient.get(BASE_URL + "api/v1/user/mypage") {
            headers["Authorization"] = "Bearer $jwt"
        }
        println(response)
        checkNotOkThrowNetworkException(response.status)

        return response.body()

    }

    fun setUserAddress(jwt: String, addressRequestDto: AddressRequestDto): Boolean {
        return runBlocking {
            val response = ktorClient.post(BASE_URL + "api/v1/user/set_address") {
                headers["Authorization"] = "Bearer $jwt"
                contentType(ContentType.Application.Json)
                setBody(addressRequestDto)
            }
            println(response)
            checkNotOkThrowNetworkException(response.status)
            return@runBlocking true
        }
    }

    suspend fun verifyPhoneNumber(phoneNumber: String): PhoneNumberVerifyResponse {
        val response = ktorClient.post(BASE_URL + "api/v1/login/verify_phone") {
            contentType(ContentType.Application.Json)
            setBody(PhoneNumberVerifyDto(phoneNumber))
        }
        println(response)
        if(response.status ==HttpStatusCode.BadRequest){
            val body : ErrorResponse? = response.body() as? ErrorResponse
            println(body)
            if (body != null) throw PhoneNumberDuplicatedException(body.message.toString())
        }
        checkNotOkThrowNetworkException(response.status)
        return response.body()
    }

    suspend fun setPhoneNumber(jwt: String, phoneNumber: String) {
        val response = ktorClient.post(BASE_URL + "api/v1/user/set_phoneNumber") {
            headers["Authorization"] = "Bearer $jwt"
            contentType(ContentType.Application.Json)
            setBody(PhoneNumberDto(phoneNumber))
        }
        println(response)
        if(response.status ==HttpStatusCode.BadRequest){
            val body : ErrorResponse? = response.body() as? ErrorResponse
            println(body)
            if (body != null) throw PhoneNumberDuplicatedException(body.message.toString())
        }
        checkNotOkThrowNetworkException(response.status)

    }
}


@Serializable
data class PhoneNumberVerifyResponse(
    val secretKey: String
)

@Serializable
data class PhoneNumberVerifyDto(
    val phoneNumber: String
)

@Serializable
data class PhoneNumberDto(
    val phone_number: String
)


@Serializable
data class MobileUserLoginDto(
    val registrationId: String,
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val userNameAttributeNameValue: String
)

@Serializable
data class MobileLoginResponse(
    val jwt: String,
    val refreshToken: String
)

@Serializable
data class JwtTokenResponse(
    val jwt: String,
)

@Serializable
data class UserInfoResponse(
    val userId: String,
    val email: String,
    val name: String,
    val nickname: String,
    val password: String?,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val address: Address?,
    val role: String,
    val id: Int,
    val createdDate: String?,
    val updatedDate: String?,
    val deletedDate: String?
)

@Serializable
data class Address(
    val address: String,
    val addressEnglish: String,
    val bname: String,
    val jibunAddress: String,
    val jibunAddressEnglish: String,
    val roadAddress: String,
    val sido: String,
    val sigungu: String,
    val detail: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean,
    val id: Int,
    val createdDate: String?,
    val updatedDate: String?,
    val deletedDate: String?
)

@Serializable
data class AddressRequestDto(
    val address: String,
    val addressEnglish: String,
    val bname: String,
    val jibunAddress: String,
    val jibunAddressEnglish: String,
    val roadAddress: String,
    val sido: String,
    val sigungu: String,
    val detail: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean,
)

data class AddressInfo(
    val address: String,
    val addressEnglish: String,
    val bname: String,
    val jibunAddress: String,
    val jibunAddressEnglish: String,
    val roadAddress: String,
    val sido: String,
    val sigungu: String,
    val postalCode: String,
    val country: String,
    val detail: String? = null
)
@Serializable
data class PurchaseHistory(
    val raffle : RaffleResponse,
    val count : Int,
    val isWinner : Boolean
)