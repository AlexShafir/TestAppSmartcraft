package com.test.app.core.data

interface IntentLoginModel : ILoginModel {
    /**
     * Execute login only if application access was not granted before.
     * If access was already granted, UI will silently succeed, but there will be flicker.
     *
     * Call [startSilentSignIn] to check sign-in state
     */
    fun startUiSignIn()

    suspend fun startSilentSignIn()
}