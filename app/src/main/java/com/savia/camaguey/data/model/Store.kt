package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity: Store (Negocio)
 * ID interno CMP-XXXX visible solo para vendedor/admin.
 * Tipo entidad: MIPYME, TCP, PDL.
 */
@Entity(tableName = "stores")
data class Store(
    @PrimaryKey
    val id: String,               // UUID interno
    val idInterno: String,        // CMP-00001 format (vendedor/admin only)
    val nombrePublico: String,    // Nombre visible al comprador
    val tipoEntidad: String,      // MIPYME | TCP | PDL
    val representanteNombre: String,
    val representanteCI: String,
    val representanteTelefono: String,
    val licenciaEstatal: String,  // ID/Licencia estatal
    val direccionCompleta: String,
    val zona: String,             // La Caridad, Centro Histórico, etc.
    val latitud: Double,
    val longitud: Double,
    val categoriaPrincipal: String, // Alimentos, Ferretería, Aseo, Electrónica, Carnes, Servicios, Belleza, Reparaciones, Vivero, etc.
    val telefonoWhatsApp: String, // Para contacto del comprador (NO visible en texto, solo wa.me)
    val telefonoRecuperacion: String, // Para recuperación de contraseña
    val webUrl: String?,
    val fotoLocalUrl: String?,
    val horario: String,          // Ej: "Lun-Sab 8:00-17:00"
    val entregaInfo: String,      // "Entrega a domicilio", "Solo recoge en tienda", etc.
    val planDestacado: Boolean = false,
    val planTrialHasta: Long? = null, // Timestamp trial 30 días
    val suscripcionActiva: Boolean = false,
    val suscripcionVence: Long? = null,
    val verificado: Boolean = false,
    val estadoVerificacion: String = "pendiente", // pendiente | aprobado | rechazado
    val username: String,         // Para login unificado
    val passwordHash: String,     // BCrypt hash
    val rol: String = "vendedor", // vendedor | admin
    val creadoEn: Long = System.currentTimeMillis(),
    val ultimaActualizacion: Long = System.currentTimeMillis()
)