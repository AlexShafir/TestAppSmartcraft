package com.test.app.framework.network

import android.content.Context
import com.test.app.core.data.AccessToken
import com.test.app.core.data.OAuth2Api
import retrofit2.Call
import javax.inject.Inject

interface IRefreshTokenService {
    fun refreshAccessToken(refreshToken:String):Call<AccessToken>
}

class RefreshTokenService @Inject constructor(
    private val oauth:OAuth2Api,
    private val clientId:String,
    private val clientSecret:String,
): IRefreshTokenService {
    override fun refreshAccessToken(refreshToken: String): Call<AccessToken> {
        return oauth.refreshAccessToken(refreshToken, clientId, clientSecret)
    }

}