package com.test.app.framework.network

import com.test.app.core.data.ITokenStorage
import com.test.app.core.domain.usecases.IOnTokenRevokedUC
import okhttp3.*
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

// OAuth2 token pipeline

@Singleton
class AuthorizationInterceptor @Inject constructor (
    private val tokenStorage: ITokenStorage
    ) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request().newBuilder()
                .header("Authorization", "${tokenStorage.tokenType} ${tokenStorage.accessToken}")
                .build()
        )

}

@Singleton
class OAuth2Authenticator @Inject constructor(
    private val rts:IRefreshTokenService,
    private val tokenStorage: ITokenStorage,
    private val tokenListener:IOnTokenRevokedUC
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        try {
            val refreshTokenResponse = rts.refreshAccessToken(tokenStorage.refreshToken).execute()
            val sessionDataResponse = refreshTokenResponse.body()?.access_token
            if (refreshTokenResponse.isSuccessful && sessionDataResponse != null) {
                tokenStorage.accessToken = sessionDataResponse

                // retry request with the new tokens
                return response.request()
                    .newBuilder()
                    .header("Authorization", "${tokenStorage.tokenType} ${tokenStorage.accessToken}")
                    .build()
            } else {
                throw HttpException(refreshTokenResponse)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                        onSessionExpiration()
                        return null // couldn't refresh show error to the user

                }
            }
        }

        // return the request with 401 error since the refresh token failed
        return null
    }

    /**
     * On SessionExpiration we clear the data and report the event.
     */
    private fun onSessionExpiration() {
        tokenListener.onTokenRevoked()
    }
}