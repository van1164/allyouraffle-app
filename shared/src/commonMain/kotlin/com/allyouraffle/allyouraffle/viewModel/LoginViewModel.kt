package com.allyouraffle.allyouraffle.viewModel


import com.allyouraffle.allyouraffle.network.AppleSignUpDto
import com.allyouraffle.allyouraffle.network.JwtTokenResponse
import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.MobileLoginResponse
import com.allyouraffle.allyouraffle.network.MobileUserLoginDto
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import kotlinx.coroutines.runBlocking

class LoginViewModel : BaseViewModel() {
    fun jwtVerify(jwt: String): Boolean {
        return LoginApi.verify(jwt)
    }

    fun refresh(refreshToken: String): JwtTokenResponse? {
        return LoginApi.refresh(refreshToken)
    }

    fun getUserInfo(jwt: String): UserInfoResponse {
        return runBlocking {
            return@runBlocking LoginApi.getUserInfo(jwt)
        }
    }

    fun googleLogin(
        email: String,
        displayName: String,
        id: String,
        profileImageUrl: String?
    ): MobileLoginResponse? {
        return runBlocking {
            var response: MobileLoginResponse? = null
            safeApiCall {
                response = LoginApi.login(
                    MobileUserLoginDto(
                        "google",
                        email = email,
                        name = displayName,
                        profileImageUrl = profileImageUrl,
                        userNameAttributeNameValue = id
                    )
                )
            }
            return@runBlocking response
        }
    }

    fun appleRegister(
        email: String,
        displayName: String,
        id: String,
        profileImageUrl: String?,
        userId : String
    ): MobileLoginResponse? {
        return runBlocking {
            var response: MobileLoginResponse? = null
            safeApiCall {
                response = LoginApi.appleSignUp(
                    AppleSignUpDto(
                        email = email,
                        name = displayName,
                        profileImageUrl = profileImageUrl,
                        userNameAttributeNameValue = id,
                        userId = userId
                    )
                )
            }
            return@runBlocking response
        }
    }

    fun appleLogin(
        userId : String
    ) : MobileLoginResponse?{
        return runBlocking {
            var response: MobileLoginResponse? = null
            safeApiCall {
                response =  LoginApi.appleLogin(userId)
            }
            return@runBlocking response
        }
    }

    fun appleSignUp(
        appleSignUpDto: AppleSignUpDto
    ) : MobileLoginResponse?{
        return runBlocking {
            var response: MobileLoginResponse? = null
            safeApiCall {
                response =  LoginApi.appleSignUp(appleSignUpDto)
            }
            return@runBlocking response
        }

    }

}