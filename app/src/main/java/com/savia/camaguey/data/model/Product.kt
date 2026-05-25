package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [Index(value = ["tiendaId"])]
)
data class Product(
    @PrimaryKey
    val id: String,
    val tiendaId: String,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val moneda: String,
    val tipoItem: String,
    val enStock: Boolean = true,
    val porEncargo: Boolean = false,
    val ofertaFlash: Boolean = false,
    val precioOferta: Double? = null,
    val imagenUrl: String? = null,
    val ultimaEdicion: Long,
    val categoria: String
)
