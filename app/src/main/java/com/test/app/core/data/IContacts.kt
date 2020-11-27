package com.test.app.core.data

import com.test.app.core.domain.entities.Contact
import kotlinx.coroutines.flow.Flow

interface IContacts {
    fun getAllContactsLocally(): Flow<List<Contact>>
    suspend fun saveAllContactsFromRemoteLocally()
}