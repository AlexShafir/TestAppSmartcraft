package com.test.app.core.domain.interactors

import com.test.app.core.data.ITokenStorage
import com.test.app.core.domain.usecases.IOnTokenRevokedUC
import javax.inject.Inject

class OnTokenRevokedUC @Inject constructor(
    private val tokenStorage:ITokenStorage
): IOnTokenRevokedUC {

    override fun onTokenRevoked() {
        tokenStorage.clear()
        // TODO
    }

}