package com.test.app.framework.network

import com.test.app.core.data.Person
import com.test.app.core.data.Photo
import com.test.app.core.domain.entities.Contact

object PersonToContactsMapper {
    fun map(persons:List<Person>):List<Contact> {

        return persons
            .map { p -> Contact(
                p.resourceName,
                p.names[0].displayName,
                if (!p.organizations.isNullOrEmpty()) p.organizations[0].name else "",
                p.photos.toList().filter { it.default }.ifEmpty { listOf(Photo("", true)) }.single().url
                ) }


    }
}