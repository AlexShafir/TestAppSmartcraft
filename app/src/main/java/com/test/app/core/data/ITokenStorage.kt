package com.test.app.core.data

interface ITokenStorage {
    var accessToken:String
    var refreshToken:String
    var tokenType:String

    val tokenSet:Boolean

    fun clear()
}