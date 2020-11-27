package com.test.app.core.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

data class AccessToken(
    val access_token:String
)

data class AuthResponse(
    val access_token:String,
    val expires_in:Int,
    val token_type:String,
    val scope:String,
    val refresh_token:String // Actually can be null, should be filtered out by implementation
)

interface OAuth2Api {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    suspend fun auth(
        @Field("client_id") clientId:String,
        @Field("client_secret") clientSecret:String,
        @Field("code") code:String,
        @Field("grant_type") grantType:String="authorization_code",
    ): AuthResponse

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    fun refreshAccessToken(
        @Field("refresh_token") refreshToken:String,
        @Field("client_id") clientId:String,
        @Field("client_secret") clientSecret:String,
        @Field("grant_type") grantType:String="refresh_token",
    ): Call<AccessToken> // will be used inside Authenticator, see https://github.com/square/okhttp/issues/3714

}


