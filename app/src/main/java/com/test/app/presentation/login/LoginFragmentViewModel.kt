package com.test.app.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.test.app.R
import com.test.app.core.data.IConnection
import com.test.app.core.domain.usecases.ILoginUC
import com.test.app.presentation.IScreenNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO: Handle case when user signed in, than clears app data, than enters app again -
// in this case auth request will not contain refresh code, so we will need to revoke access first
// TODO: Handle Internet loss during google login activity

class LoginFragmentViewModel @Inject constructor(
    private val nav: IScreenNavigator,
    private val connection:IConnection,
    private val login: ILoginUC
) : ViewModel() {

    val internetState:LiveData<IConnection.InternetState> = connection.stateFlow.asLiveData()

    // If we are here, it means user is not authorized (token was not set)
    init {
        viewModelScope.launch {
            login.onTokenObtained
                .collect {
                    nav.navTo(R.id.action_login_success)
                }
        }

    }

    fun onSignUpButtonClick() {
        login.startUiSignIn()
    }

}