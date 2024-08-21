package com.allyouraffle.allyouraffle.android.login

import android.annotation.SuppressLint
import android.util.Log
import com.allyouraffle.allyouraffle.exception.LoginException
import com.allyouraffle.allyouraffle.exception.NetworkException
import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.MobileUserLoginDto
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    fun jwtVerify(jwt: String): Boolean {
        return LoginApi.verify(jwt)
    }

    fun refresh(refreshToken: String): String? {
        return LoginApi.refresh(refreshToken)
    }

    fun getUserInfo(jwt: String): UserInfoResponse {
        return LoginApi.getUserInfo(jwt)
    }


    @SuppressLint("CommitPrefEdits")
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>?): HashMap<String, String>? {
        if (task == null) {
            setError()
            return null
        }
        try {
            setLoading()
            val account = task.getResult(ApiException::class.java)
            account?.let {
                Log.d("LoginViewModel", "Google sign in success: ${account.id}")
                val id = account.id ?: kotlin.run { throw LoginException("id 없음") }
                val email = account.email ?: kotlin.run { throw LoginException("email 없음") }
                val displayName =
                    account.displayName ?: kotlin.run { throw LoginException("이름 없음") }
                println(email)
                println(displayName)
                val response = LoginApi.login(
                    MobileUserLoginDto(
                        "google",
                        email = email,
                        name = displayName,
                        profileImageUrl = account.photoUrl?.path.toString(),
                        userNameAttributeNameValue = id
                    )
                )
                endLoading()
                return hashMapOf("JWT" to response.jwt, "REFRESH" to response.refreshToken)
            } ?: run {
                setError()
            }
        } catch (e: ApiException) {
            Log.e("LoginViewModel", "Google sign in failed: ${e.statusCode}")
            setError()
        } catch (e: LoginException) {
            Log.e("LoginViewModel", "Login Fail: ${e.message}")
            setError()
        } catch (e: NetworkException) {
            Log.e("LoginViewModel", "Login Fail: Networ Error")
            setError()
        } finally {
            endLoading()
        }
        return null
    }

    fun setLoading() {
        _isLoading.update {
            true
        }
    }

    fun endLoading() {
        _isLoading.update {
            false
        }
    }

    fun setError() {
        _error.update {
            true
        }
    }

    fun endError() {
        _error.update {
            false
        }
    }

}