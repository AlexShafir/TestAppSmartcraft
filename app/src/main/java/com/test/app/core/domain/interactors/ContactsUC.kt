package com.test.app.core.domain.interactors

import com.test.app.core.data.IContacts
import com.test.app.core.domain.entities.Contact
import com.test.app.core.domain.usecases.IContactsUC
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsUC @Inject constructor(
    private val repo:IContacts
):IContactsUC {

    private val mSyncing = MutableStateFlow(false)
    override val syncing: StateFlow<Boolean> = mSyncing

    override fun getAllContactsLocally(): Flow<List<Contact>> {
        return repo.getAllContactsLocally()
    }

    override fun syncAllContacts() {
        GlobalScope.launch {
            mSyncing.value = true
            repo.saveAllContactsFromRemoteLocally()
            mSyncing.value = false
        }
    }

}