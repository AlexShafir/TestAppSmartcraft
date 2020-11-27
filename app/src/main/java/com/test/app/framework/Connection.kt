package com.test.app.framework

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.test.app.core.data.IConnection
import com.test.app.core.data.IConnection.InternetState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Connection @Inject constructor(context: Context) : IConnection {

    private val mStateFlow:MutableStateFlow<InternetState> = MutableStateFlow(InternetState.NONE)
    override val stateFlow: Flow<InternetState> = mStateFlow.filter { it != InternetState.NONE }

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val state: InternetState
        get() = mStateFlow.value

    /*
     * The only non-deprecated way to listen for network state on Android is registerNetworkCallback, that supports min Android 5.0
     *
     * API description is a bit misleading: onAvailable gets called even for networks that became active before callback was registered.
     * Hint is in hidden onPreCheck method: "Called when the framework connects to a new network to evaluate whether it satisfies this request.
     * If evaluation succeeds, this callback may be followed by an {@link #onAvailable} callback"
     *
     * Test device: Xiaomi Redmi 8T (Android 9.0)
     * Both cellular and WiFi enabled: size is 1
     *    Disable WiFi - size goes 0, than returns to 1
     * Nothing enabled: size is 0
     *    Enable Cellular: size is 1. Additionally enable WiFi: size is 2
     */
    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            // val t = cm.allNetworks.size
            if(cm.allNetworks.isNotEmpty() && state != InternetState.ONLINE) {
                mStateFlow.value = InternetState.ONLINE
            }
        }

        override fun onLost(network: Network) {
            // val t = cm.allNetworks.size
            if(cm.allNetworks.isEmpty() && state != InternetState.OFFLINE) {
                // Allow delay for network switch
                GlobalScope.launch {
                    delay(2000)
                    if (cm.allNetworks.isEmpty()) mStateFlow.value = InternetState.OFFLINE
                }
            }
        }


    }

    init {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        // Set initial state
        if(cm.allNetworks.isNotEmpty()) mStateFlow.value = InternetState.ONLINE
        else mStateFlow.value = InternetState.OFFLINE

        cm.registerNetworkCallback(request, callback) // gets unregistered on process death
    }
}