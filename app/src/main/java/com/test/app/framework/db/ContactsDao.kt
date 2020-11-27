package com.test.app.framework.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.app.core.domain.entities.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(contacts:List<Contact>)

    @Query("SELECT * FROM Contact")
    fun getAllContacts(): Flow<List<Contact>>

}