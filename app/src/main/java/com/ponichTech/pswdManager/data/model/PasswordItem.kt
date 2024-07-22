package com.ponichTech.pswdManager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "password_items")
data class PasswordItem(
    @PrimaryKey val id:String = UUID.randomUUID().toString(),
    val serviceName: String = "",
    val username: String = "",
    val password: String = "",
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val photo: String? = "",
    val userId:String = ""
)