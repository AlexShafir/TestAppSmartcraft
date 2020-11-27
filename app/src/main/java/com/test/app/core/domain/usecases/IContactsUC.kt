package com.test.app.core.domain.usecases

import com.test.app.core.domain.entities.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IContactsUC {
    val syncing: StateFlow<Boolean>
    fun getAllContactsLocally(): Flow<List<Contact>>
    fun syncAllContacts()
}