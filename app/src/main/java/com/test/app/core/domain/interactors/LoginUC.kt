package com.test.app.core.domain.interactors

import com.test.app.core.data.IContacts
import com.test.app.core.data.ITokenStorage
import com.test.app.core.data.IntentLoginModel
import com.test.app.core.domain.usecases.ILoginUC
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginUC @Inject constructor(
    private val loginModel: IntentLoginModel,
    private val tokenStorage: ITokenStorage,
    private val contacts: IContacts
): ILoginUC {
    private val mOnTokenObtained = MutableStateFlow(false)
    override val onTokenObtained: Flow<Boolean> = mOnTokenObtained.filter { it }

    init {
        GlobalScope.launch {
            loginModel.tokenFlow
                .collect {
                    tokenStorage.accessToken = it.access_token
                    tokenStorage.refreshToken = it.refresh_token
                    tokenStorage.tokenType = it.token_type

                    mOnTokenObtained.value = true

                    // Immediately launch contact load
                    contacts.saveAllContactsFromRemoteLocally()
                }
        }
    }

    override fun startUiSignIn() {
        loginModel.startUiSignIn()
    }

}