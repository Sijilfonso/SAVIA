package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: AdminAccount (Cuentas de administrador para login unificado)
 * Solo 2 cuentas hardcodeadas en seed. NO registro público.
 */
@Entity(
    tableName = "admin_accounts",
    indices = [Index(value = ["username"], unique = true)]
)
data class AdminAccount(
    @PrimaryKey
    val id: String,               // UUID
    val username: String,         // savia.admin1, savia.admin2
    val passwordHash: String,     // BCrypt con cost 12
    val nombre: String,
    val telefonoRecuperacion: String,
    val rol: String = "admin",    // admin
    val creadoEn: Long = System.currentTimeMillis()
)
