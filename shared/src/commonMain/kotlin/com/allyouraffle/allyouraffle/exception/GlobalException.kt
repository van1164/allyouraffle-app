package com.allyouraffle.allyouraffle.exception

class JwtNotFoundException : RuntimeException()
class DetailServiceException : RuntimeException()
class RefreshTokenNotFoundException : RuntimeException()
class NotificationException(override val message: String = "알림 오류") : RuntimeException()