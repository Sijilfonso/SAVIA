package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class Store(
    @PrimaryKey
    val id: String,
    val idInterno: String,
    val nombrePublico: String,
    val tipoEntidad: String,
    val zona: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String,
    val horario: String,
    val entregaDisponible: Boolean,
    val radioEntregaKm: Int,
    val webUrl: String? = null,
    val planSuscripcion: String,
    val suscripcionActiva: Boolean,
    val fechaRegistro: Long,
    val ultimaConfirmacionStock: Long? = null,
    val verificado: Boolean = false,
    val destacado: Boolean = false,
    val categoriaPrincipal: String,
    val username: String? = null,
    val passwordHash: String? = null,
    val telefonoRecuperacion: String? = null,
    val permiteReservas: Boolean = false,
    val fotoLocalUrl: String? = null,
    val descripcion: String? = null
)
