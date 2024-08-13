package com.allyouraffle.allyouraffle.network

class NetworkException : RuntimeException()
class LoginException(override val message : String) : RuntimeException()