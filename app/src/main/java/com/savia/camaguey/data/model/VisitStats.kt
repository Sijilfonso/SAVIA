package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visit_stats",
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["productoId"]),
        Index(value = ["fecha"])
    ]
)
data class VisitStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val productoId: String? = null,
    val tipo: String,
    val fecha: String,
    val timestamp: Long = System.currentTimeMillis()
)
