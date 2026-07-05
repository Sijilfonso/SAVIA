package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: PasswordReset (Códigos de recuperación de contraseña)
 * Código numérico de 6 dígitos, expira en 15 minutos.
 * Enviado vía WhatsApp Business API.
 */
@Entity(
    tableName = "password_resets",
    indices = [
        Index(value = ["username"]),
        Index(value = ["codigo"]),
        Index(value = ["expiraEn"])
    ]
)
data class PasswordReset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,         // username de la cuenta (vendedor o admin)
    val codigo: String,           // 6 dígitos numéricos
    val telefonoDestino: String,
    val creadoEn: Long = System.currentTimeMillis(),
    val expiraEn: Long = System.currentTimeMillis() + (15 * 60 * 1000), // 15 min
    val usado: Boolean = false
)
