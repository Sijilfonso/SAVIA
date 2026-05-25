package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_accounts")
data class AdminAccount(
    @PrimaryKey
    val username: String,
    val passwordHash: String,
    val rol: String = "admin",
    val telefonoRecuperacion: String,
    val creadoEn: Long = System.currentTimeMillis()
)
