package com.test.app.core.data

import kotlinx.coroutines.flow.Flow


/**
 * Implements sign-in flow only (no token management)
 */
interface ILoginModel {

    enum class LoginState {
        SIGN_IN_REQUIRED, SIGNED_IN, INTERRUPTED_BY_USER
    }

    // Platform leak - nothing to do with it currently
    val authFlow: Flow<LoginState>
    val tokenFlow:Flow<AuthResponse>

    suspend fun signOut()

    /**
     * Note: Requires sign-in first, otherwise ApiException
     */
    suspend fun revokeAccess()
}