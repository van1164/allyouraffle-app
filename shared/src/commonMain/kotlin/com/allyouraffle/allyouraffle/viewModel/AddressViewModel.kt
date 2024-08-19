package com.allyouraffle.allyouraffle.viewModel

import com.allyouraffle.allyouraffle.network.Address
import com.allyouraffle.allyouraffle.network.AddressInfo
import com.allyouraffle.allyouraffle.network.AddressRequestDto
import com.allyouraffle.allyouraffle.network.LoginApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddressViewModel {
    private val _userAddress = MutableStateFlow<AddressInfo?>(null)
    val userAddress = _userAddress.asStateFlow()

    private val _detail = MutableStateFlow<String>("")
    val detail = _detail.asStateFlow()

    fun setDetail(data: String) {
        _detail.update {
            data
        }
    }

    fun setUserAddress(address: AddressInfo) {
        _userAddress.update {
            address
        }
    }

    fun saveUserAddress(jwt : String): Boolean {
        val finalAddress = _userAddress.value
        checkNotNull(finalAddress)
        val addressRequest = AddressRequestDto(
            address = finalAddress.address,
            addressEnglish = finalAddress.addressEnglish,
            bname = finalAddress.bname,
            jibunAddress = finalAddress.jibunAddress,
            jibunAddressEnglish = finalAddress.jibunAddressEnglish,
            roadAddress = finalAddress.roadAddress,
            sido = finalAddress.sido,
            sigungu = finalAddress.sigungu,
            detail = _detail.value,
            postalCode = finalAddress.postalCode,
            country = finalAddress.country,
            isDefault = true
        )

        return LoginApi.setUserAddress(jwt,addressRequest)
    }
}