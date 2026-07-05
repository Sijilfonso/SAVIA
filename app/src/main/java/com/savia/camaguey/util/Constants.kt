package com.savia.camaguey.util

import android.content.Context
import android.provider.Settings

/**
 * Constantes globales de SAVIA.
 * Incluye traducciones de tipo_entidad para UI comprador/vendedor.
 */
object Constants {

    // Database
    const val DATABASE_NAME = "savia_database.db"
    const val SEED_VERSION = 1

    // Backend (future)
    const val API_BASE_URL = "https://api.savia.cu/"
    const val API_TIMEOUT_SECONDS = 30L

    // WhatsApp fallback support number
    const val SAVIA_SUPPORT_PHONE = "+5355559999"

    // Location default: center of Camagüey
    const val DEFAULT_LAT = 21.3833
    const val DEFAULT_LNG = -77.9167

    // Stock margin in days
    const val STOCK_MARGIN_DAYS = 30

    // Subscription trial days
    const val TRIAL_DAYS = 30

    // Password reset expiration minutes
    const val RESET_CODE_EXPIRY_MINUTES = 15

    // Admin password constraints
    const val ADMIN_PASSWORD_MIN_LENGTH = 12

    // Vendor password constraints
    const val VENDOR_PASSWORD_MIN_LENGTH = 8

    // Entity type display names for BUYER UI (lenguaje simple)
    fun entityTypeToDisplayName(tipo: String): String {
        return when (tipo.uppercase()) {
            "MIPYME" -> "Empresa local"
            "TCP" -> "Negocio personal"
            "PDL" -> "Proyecto comunitario"
            else -> tipo
        }
    }

    // Entity type display names for VENDOR/ADMIN UI (siglas originales)
    fun entityTypeToTechnicalName(tipo: String): String {
        return when (tipo.uppercase()) {
            "MIPYME" -> "MIPYME"
            "TCP" -> "TCP"
            "PDL" -> "PDL"
            else -> tipo
        }
    }

    // Stock state display names for buyer UI
    fun stockStateToDisplayName(estado: String, tipoItem: String): String {
        return when (estado) {
            "disponible" -> if (tipoItem == "servicio") "Disponible" else "En stock"
            "agotado" -> "Agotado"
            "por_encargo" -> "Por encargo"
            "no_disponible" -> "No disponible"
            else -> estado
        }
    }

    // Currency display symbols
    fun currencySymbol(moneda: String): String {
        return when (moneda.uppercase()) {
            "CUP" -> "$")
            "USD" -> "USD"
            "MLC" -> "MLC"
            else -> moneda
        }
    }

    // Generate anonymous device ID (does not identify person)
    fun getAnonymousDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ?: "unknown_${System.currentTimeMillis()}"
        } catch (e: Exception) {
            "fallback_${System.currentTimeMillis()}"
        }
    }

    // Categories for UI filters
    val CATEGORIAS = listOf(
        "Alimentos", "Ferretería", "Aseo", "Electrónica",
        "Carnes", "Belleza", "Reparaciones", "Vivero"
    )

    // Zonas de Camagüey
    val ZONAS = listOf(
        "La Caridad", "Centro Histórico", "Vista Hermosa",
        "Santa Rosa", "Santa Elena"
    )

    // Moneda options
    val MONEDAS = listOf("CUP", "USD", "MLC", "AMBAS")

    // Transport modes
    val TRANSPORT_MODES = listOf("Caminando", "Moto", "Auto")
}
