package com.allyouraffle.allyouraffle.network

class NetworkException(override val message: String = "네트워크 에러") : RuntimeException()
class LoginException(override val message : String) : RuntimeException()