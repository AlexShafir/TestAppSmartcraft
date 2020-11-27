package com.test.app.core.domain.usecases

interface IScreenSelectorUC {
    enum class ScreenType {
        SIGN_UP, CONTACTS
    }

    val initialScreen:ScreenType
}