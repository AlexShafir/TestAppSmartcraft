package com.test.app.core.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey val uid:String,
    val name:String,
    val company:String,
    val avatarUrl:String
)