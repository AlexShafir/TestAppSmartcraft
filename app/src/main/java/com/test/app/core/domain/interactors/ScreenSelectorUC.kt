package com.test.app.core.domain.interactors

import com.test.app.core.data.ITokenStorage
import com.test.app.core.domain.usecases.IScreenSelectorUC
import com.test.app.core.domain.usecases.IScreenSelectorUC.ScreenType
import javax.inject.Inject

class ScreenSelectorUC @Inject constructor(
    private val tokenStorage: ITokenStorage
):IScreenSelectorUC {

    override val initialScreen: ScreenType
        get() {
            if(tokenStorage.tokenSet) {
                return ScreenType.CONTACTS
            } else {
                return ScreenType.SIGN_UP
            }
        }
}