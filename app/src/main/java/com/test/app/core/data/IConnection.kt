package com.test.app.core.data

import kotlinx.coroutines.flow.Flow

interface IConnection {
    enum class InternetState {
        NONE, ONLINE, OFFLINE
    }

    val stateFlow: Flow<InternetState>

    val state:InternetState
}