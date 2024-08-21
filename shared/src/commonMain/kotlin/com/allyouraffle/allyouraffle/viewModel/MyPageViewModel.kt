package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyPageViewModel {
    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(true)
    val loading = _loading.asStateFlow()

    fun getUserInfo(jwt: String) {
        _loading.update { true }
        _userInfo.update { LoginApi.getUserInfo(jwt) }
        _loading.update { false }
    }

}