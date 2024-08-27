package com.allyouraffle.allyouraffle.exception

class NetworkException(override val message: String = "네트워크 에러") : RuntimeException()
class JwtException(override val message: String = "다시 시도해주세요.") : RuntimeException()
class LoginException(override val message : String) : RuntimeException()
class RaffleNotFoundException(override val message: String) : RuntimeException()
class PurchaseException(override val message: String):RuntimeException()
class PhoneNumberDuplicatedException(override val message: String):RuntimeException()