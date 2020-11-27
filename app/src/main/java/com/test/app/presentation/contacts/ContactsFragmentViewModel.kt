package com.test.app.presentation.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.test.app.core.data.IConnection
import com.test.app.core.domain.entities.Contact
import com.test.app.core.domain.usecases.IContactsUC
import javax.inject.Inject

class ContactsFragmentViewModel @Inject constructor(
    private val contacts: IContactsUC,
    private val connection:IConnection
) : ViewModel() {
    val contactsFlow: LiveData<List<Contact>> = contacts.getAllContactsLocally().asLiveData()
    val syncingFlow:LiveData<Boolean> = contacts.syncing.asLiveData()
    val internetState:LiveData<IConnection.InternetState> = connection.stateFlow.asLiveData()

    fun onSyncClick() {
        contacts.syncAllContacts()
    }
}