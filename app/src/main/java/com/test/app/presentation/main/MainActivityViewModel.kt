package com.test.app.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.test.app.core.data.GooglePeopleApi
import com.test.app.core.data.IContacts
import com.test.app.core.data.OAuth2Api
import com.test.app.core.domain.usecases.IScreenSelectorUC
import com.test.app.core.domain.usecases.IScreenSelectorUC.ScreenType
import com.test.app.framework.VolatileLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
    private val screenSelector: IScreenSelectorUC,
) : ViewModel() {

    private val mScreenState = VolatileLiveData<ScreenType>()

    /**
     * Hot stream
     */
    val screenType: LiveData<ScreenType> = mScreenState

    fun onAppCreate() {
        when(screenSelector.initialScreen) {
            ScreenType.SIGN_UP -> mScreenState.setValue(ScreenType.SIGN_UP)
            ScreenType.CONTACTS -> mScreenState.setValue(ScreenType.CONTACTS)
        }
    }

}