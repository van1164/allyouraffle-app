package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.LoginApi
import com.allyouraffle.allyouraffle.network.UserInfoResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhoneNumberViewModel {

    private val _phoneNumber = MutableStateFlow<String>("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()

    private val _verifying = MutableStateFlow<Boolean>(false)
    val verifying = _verifying.asStateFlow()

    private val _verifyNumber = MutableStateFlow<String?>(null)
    val verifyNumber = _verifyNumber.asStateFlow()

    fun setPhoneNumber(number: String) {
        _phoneNumber.update { number }
    }

    fun verifyPhoneNumber() {
        val response = LoginApi.verifyPhoneNumber(formatPhoneNumber(_phoneNumber.value))?.secretKey
        _verifyNumber.update { response }
        _verifying.update { true }
    }

    fun verifyTest(){
        _verifyNumber.update { "123456" }
        _verifying.update { true }
    }

    fun savePhoneNumber(jwt: String): Boolean {
        _loading.update { true }
        val response =  LoginApi.setPhoneNumber(jwt,formatPhoneNumber(_phoneNumber.value))
        _loading.update { false }
        return response
    }

    private fun formatPhoneNumber(input: String): String {
        // 숫자만 추출
        val digits = input.filter { it.isDigit() }

        // 길이가 10 또는 11인 경우에만 포맷팅
        return when (digits.length) {
            10 -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
            11 -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
            else -> digits
        }
    }

}