package com.test.app.framework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.app.core.domain.entities.Contact

@Database(entities = [Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactsDao(): ContactsDao
}