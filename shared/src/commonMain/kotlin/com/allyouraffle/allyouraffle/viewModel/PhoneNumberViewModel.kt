package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.exception.PhoneNumberDuplicatedException
import com.allyouraffle.allyouraffle.network.LoginApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhoneNumberViewModel : BaseViewModel() {

    private val _phoneNumber = MutableStateFlow<String>("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _verifying = MutableStateFlow<Boolean>(false)
    val verifying = _verifying.asStateFlow()

    private val _verifyNumber = MutableStateFlow<String?>(null)
    val verifyNumber = _verifyNumber.asStateFlow()

    private val _numberSaved = MutableStateFlow<Boolean>(false)
    val numberSaved = _numberSaved.asStateFlow()
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun setPhoneNumber(number: String) {
        _phoneNumber.update { number }
    }

    suspend fun verifyPhoneNumber() {
        println("여기는 번호 인증 : "+ phoneNumber.value.toString())
        safeApiCall {
            try {
                val response =
                    LoginApi.verifyPhoneNumber(formatPhoneNumber(_phoneNumber.value)).secretKey
                _verifying.update { true }
                _verifyNumber.update { response }
            } catch (
                e: PhoneNumberDuplicatedException

            ) {
                _verifying.update { false }
                _verifyNumber.update { null }
                _phoneNumber.update { "" }
                _error.update { "이미 등록된 전화번호입니다." }
            }

        }
    }

    fun verifyTest() {
        _verifyNumber.update { "123456" }
        _verifying.update { true }
    }

    suspend fun savePhoneNumber(jwt: String) {
        coroutineScope.launch {
            safeApiCall {
                try {
                    LoginApi.setPhoneNumber(jwt, formatPhoneNumber(_phoneNumber.value))
                    _numberSaved.update { true }
                } catch (
                    e: PhoneNumberDuplicatedException
                ) {
                    _verifying.update { false }
                    _verifyNumber.update { null }
                    _phoneNumber.update { "" }
                    _error.update { "이미 등록된 전화번호입니다." }
                }

            }
        }
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