package com.allyouraffle.allyouraffle.android.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.LoginException
import com.allyouraffle.allyouraffle.network.MobileUserLoginDto
import com.allyouraffle.allyouraffle.network.NetworkException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    @SuppressLint("StaticFieldLeak") private val context : Context
) :ViewModel(){
    private val _loginApiState = MutableStateFlow<ApiState>(ApiState.Before)
    val loginApiState = _loginApiState.asStateFlow()
    @SuppressLint("CommitPrefEdits")
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
        if (task == null) {
            _loginApiState.value = ApiState.Error
            return
        }
        try {
            _loginApiState.value = ApiState.Loading
            val account = task.getResult(ApiException::class.java)
            account?.let {
                Log.d("LoginViewModel", "Google sign in success: ${account.id}")
                val id = account.id?: kotlin.run { throw  LoginException("id 없음")}
                val email = account.email?: kotlin.run { throw  LoginException("email 없음")}
                val displayName = account.displayName?: kotlin.run { throw LoginException("이름 없음") }
                println(email)
                println(displayName)
                val response = LoginApi.login(MobileUserLoginDto(
                    "google",
                    email = email,
                    name = displayName,
                    profileImageUrl = account.photoUrl?.path.toString(),
                    userNameAttributeNameValue = id
                ))
                Log.d("JJJJJJWWWWWWWWWWW",response.jwt)
                val sharedPreferences = context.getSharedPreferences("AUTH",Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("JWT",response.jwt)
                _loginApiState.value = ApiState.Success
            } ?: run {
                _loginApiState.value = ApiState.Error
            }
        } catch (e: ApiException) {
            Log.e("LoginViewModel", "Google sign in failed: ${e.statusCode}")
            _loginApiState.value = ApiState.Error
        } catch (e : LoginException){
            Log.e("LoginViewModel", "Login Fail: ${e.message}")
            _loginApiState.value = ApiState.Error
        } catch (e : NetworkException){
            Log.e("LoginViewModel", "Login Fail: Networ Error")
            _loginApiState.value = ApiState.Error
        }
    }

    enum class ApiState(val message : String) {
        Before("로그인 전"),
        Loading("로딩중"),
        Error("Google 로그인 실패"),
        Success("Google 로그인 성공")

    }
}