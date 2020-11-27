package com.test.app.framework

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.test.app.R
import com.test.app.core.data.ITokenStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(context: Context): ITokenStorage {

    companion object {
        private const val KEY_ACCESS_TOKEN = "KeyAccessToken"
        private const val KEY_REFRESH_TOKEN = "KeyRefreshToken"
        private const val KEY_TOKEN_TYPE = "KeyTokenType"
    }

    private val prefs:SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.shared_prefs_name),
        Context.MODE_PRIVATE
    )

    // In synchronous way
    override var accessToken: String
        get() {
            val temp = prefs.getString(KEY_ACCESS_TOKEN, "")
            if(temp == "" || temp == null) throw UninitializedPropertyAccessException("No access token was previously set")
            return temp
        }
        set(value) {
            prefs.edit(commit = true) {
                putString(KEY_ACCESS_TOKEN, value)
            }
        }

    // In synchronous way
    override var refreshToken: String
        get() {
            val temp = prefs.getString(KEY_REFRESH_TOKEN, "")
            if(temp == "" || temp == null) throw UninitializedPropertyAccessException("No refresh token was previously set")
            return temp
        }
        set(value) {
            prefs.edit(commit = true) {
                putString(KEY_REFRESH_TOKEN, value)
            }
        }

    override var tokenType: String
        get() {
            val temp = prefs.getString(KEY_TOKEN_TYPE, "")
            if(temp == "" || temp == null) throw UninitializedPropertyAccessException("No token type was previously set")
            return temp
        }
        set(value) {
            prefs.edit(commit = true) {
                putString(KEY_TOKEN_TYPE, value)
            }
        }

    override val tokenSet: Boolean = prefs.contains(KEY_REFRESH_TOKEN)

    override fun clear() {
        prefs.edit(commit = true) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_TYPE)
        }
    }

}