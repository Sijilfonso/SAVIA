package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: CartItem (Item en carrito del comprador anónimo)
 * Todo 100% offline en Room. NO hay tabla de usuarios compradores.
 */
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tiendaId"]), Index(value = ["productoId"])]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,           // Para agrupar por negocio
    val productoId: String,
    val nombreProducto: String,   // Denormalizado para offline
    val precioUnitario: Double,
    val moneda: String,           // "CUP" | "USD" | "MLC"
    val cantidad: Int = 1,
    val agregadoEn: Long = System.currentTimeMillis()
)