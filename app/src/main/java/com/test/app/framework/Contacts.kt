package com.test.app.framework

import com.test.app.core.data.GooglePeopleApi
import com.test.app.core.data.IContacts
import com.test.app.core.domain.entities.Contact
import com.test.app.framework.network.PersonToContactsMapper
import com.test.app.framework.db.ContactsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// Database is SSOT
@Singleton
class Contacts @Inject constructor(
    private val webservice:GooglePeopleApi,
    private val db: ContactsDao
) : IContacts {

    private suspend fun insertOrUpdateAll(contacts: List<Contact>) {
        db.insertOrUpdateAll(contacts)
    }

    override fun getAllContactsLocally(): Flow<List<Contact>> {
        return db.getAllContacts()
    }

    override suspend fun saveAllContactsFromRemoteLocally() {
        // Fetch from remote
        val res = webservice.listAllContacts("names,photos,organizations")
        val contacts = PersonToContactsMapper.map(res.connections.toList())

        // Save locally
        insertOrUpdateAll(contacts)
    }

}

