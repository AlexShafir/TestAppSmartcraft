package com.test.app.framework

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.test.app.core.data.AuthResponse
import com.test.app.core.data.ILoginModel.LoginState
import com.test.app.core.data.IntentLoginModel
import com.test.app.core.data.OAuth2Api
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.ref.WeakReference
import javax.inject.Inject

// TODO: Handle Network Error

/*
 * This object is present in both activity and view-models (models), and we need same object everywhere (singleton).
 *
 * As models can be initialized prior to this object gets properly initialized,
 * on initialization it is safe to listen to Flow's only and call startSilentSignIn (as it reports result via Flow).
 *
 * As viewmodels are tied to lifecycle for initialization, this issue does not exist for them.
 *
 */

/**
 * Encapsulates Google OAuth2 authorization logic.
 *
 * See Also: [Google OAuth2 Native app Flow](https://developers.google.com/identity/protocols/oauth2/native-app)
 *
 * See Also: [Google OAuth2 General Flow](https://developers.google.com/identity/protocols/oauth2/openid-connect)
 *
 * See Also: [Android Sign-in sample app by Google](https://github.com/googlesamples/google-services/tree/master/android/signin)
 *
 * Note: In case user already logged in, but wants to change account, you need to sign out first.
 *
 * Note: ApiException's are not catched.
 *
 * Note: class does not save it's state.
 *
 * In future underlying implementation will use Google Identity Services (GIS), once they provide authorization.
 *
 * See Also: [Google Identity Services](ttps://developers.google.com/identity/sign-in/android/sign-in-identity)
 *
 */

@ExperimentalCoroutinesApi
class GoogleLoginModel @Inject constructor(
    private val server:OAuth2Api,
    private val clientId:String,
    private val clientSecret:String,
) : IntentLoginModel, ActivityResultClient {

    companion object {
        private lateinit var activity:WeakReference<Activity>

        fun setActivity(activityW: Activity) {
            activity = WeakReference(activityW)
        }
    }

    private var client: GoogleSignInClient
    private val RC_SIGN_IN_UI = 2000

    private val mAuthState:MutableStateFlow<LoginState?> = MutableStateFlow(null)
    private val mTokenFlow: MutableStateFlow<AuthResponse?> = MutableStateFlow(null)

    override val tokenFlow = mTokenFlow.filterNotNull()
    override val authFlow = mAuthState.filterNotNull()

    private val mIntentFlow = VolatileLiveData<IntentRequest>()
    override val intentFlow: LiveData<IntentRequest> = mIntentFlow

    init {

        val mScopes:List<Scope> = appGoogleScopes.map { Scope(it) }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(clientId)
            .requestScopes(mScopes[0], *mScopes.drop(1).toTypedArray())
            .build()

        client = GoogleSignIn.getClient(activity.get()!!, gso) // does not store activity in field
    }

    /**
     * Execute login only if application access was not granted before.
     * If access was already granted, UI will silently succeed, but there will be flicker.
     *
     * Call [startSilentSignIn] to check sign-in state
     */
    override fun startUiSignIn() {
        mIntentFlow.setValue(IntentRequest(client.signInIntent, RC_SIGN_IN_UI))
    }

    override suspend fun startSilentSignIn() {
        val task = client.silentSignIn()
        if (task.isSuccessful()) {
            mAuthState.value = LoginState.SIGNED_IN
        } else {
            obtainTokenFromTask(task)
        }
    }

    override suspend fun signOut() {
        client.signOut().await()
    }

    /**
     * Note: Requires sign-in first, otherwise ApiException
     */
    override suspend fun revokeAccess() {
        client.revokeAccess().await()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode != RC_SIGN_IN_UI) {
            mAuthState.value = LoginState.INTERRUPTED_BY_USER
            return
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        GlobalScope.launch {
            obtainTokenFromTask(task)
        }

    }

    private suspend fun obtainTokenFromTask(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.await()
            val authCode:String? = account!!.serverAuthCode

            if (authCode != null) {
                val res = server.auth(
                    clientId,
                    clientSecret,
                    authCode
                )

                // Refresh token can be null - it means that sign in was already done for this device
                // Drop response in this case
                if(res.refresh_token != null) {
                    mTokenFlow.value = res
                    mAuthState.value = LoginState.SIGNED_IN
                }

            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            when(e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> mAuthState.value = LoginState.SIGN_IN_REQUIRED
                else -> throw e
            }
        }
    }

}