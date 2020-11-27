package com.test.app.core.domain.usecases

import kotlinx.coroutines.flow.Flow

interface ILoginUC {
    val onTokenObtained: Flow<Boolean>

    fun startUiSignIn()
}