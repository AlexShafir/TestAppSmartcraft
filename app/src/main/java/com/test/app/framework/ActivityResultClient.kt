package com.test.app.framework

import android.content.Intent
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.MutableStateFlow

interface ActivityResultClient {

    /**
     * Hot stream
     */
    val intentFlow: LiveData<IntentRequest>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

data class IntentRequest(
    val intent: Intent,
    val requestCode:Int
)