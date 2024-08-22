package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyPageViewModel : BaseViewModel(){
    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo = _userInfo.asStateFlow()

    suspend fun initUserInfo(jwt:String){
        if(_userInfo.value ==null){
            getUserInfo(jwt)
        }
    }

    suspend fun getUserInfo(jwt: String) {
        safeApiCall {
            _userInfo.update { LoginApi.getUserInfo(jwt) }
        }
    }

}