package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: Product (Producto o Servicio)
 * tipoItem = "producto" | "servicio"
 * Servicios están exentos de penalización por stock.
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tiendaId"]), Index(value = ["categoria"]), Index(value = ["tipoItem"])]
)
data class Product(
    @PrimaryKey
    val id: String,               // UUID interno
    val tiendaId: String,         // FK a stores.id
    val nombre: String,
    val descripcion: String?,
    val categoria: String,        // Coincide con Store.categoriaPrincipal o subcategoría
    val tipoItem: String,         // "producto" | "servicio"
    val precioCUP: Double?,
    val precioUSD: Double?,
    val precioMLC: Double?,       // Moneda Libremente Convertible (opcional)
    val monedaMostrar: String,    // "CUP" | "USD" | "MLC" | "AMBAS"
    val estadoStock: String,      // "disponible" | "agotado" | "por_encargo" | "no_disponible"
    val ofertaFlash: Boolean = false,
    val precioOfertaCUP: Double? = null,
    val precioOfertaUSD: Double? = null,
    val fotoUrl: String?,
    val fotoUrls: List<String>?,  // Galería (usar TypeConverter)
    val stockCantidad: Int? = null, // Solo para productos
    val unidadMedida: String? = null, // kg, lb, unidad, etc.
    val disponibleDesde: String? = null, // Para servicios: "Lun-Vie", etc.
    val tiempoEntrega: String? = null, // "24h", "Inmediato", etc.
    val tags: List<String>?,
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val creadoEn: Long = System.currentTimeMillis()
)