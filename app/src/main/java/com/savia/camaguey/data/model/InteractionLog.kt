package com.savia.camaguey.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity: InteractionLog (Logs de interacción del comprador anónimo)
 * Usado para métricas en el panel del vendedor.
 * NO contiene datos personales identificables del comprador.
 * El teléfono del visitante SOLO se almacena si el vendedor lo recibe explícitamente
 * y solo es visible en el panel privado de esa tienda.
 */
@Entity(
    tableName = "interaction_logs",
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["tipo"]),
        Index(value = ["fecha"])
    ]
)
data class InteractionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,           // Negocio que recibió la interacción
    val productoId: String? = null, // Producto visto (null si es visita al perfil)
    val tipo: String,               // "perfil_visto" | "producto_visto" | "click_whatsapp" | "busqueda" | "producto_agregado_carrito"
    val queryBusqueda: String? = null, // Solo para tipo = "busqueda"
    val dispositivoId: String,      // ID anónimo del dispositivo (NO vinculado a persona)
    val fecha: Long = System.currentTimeMillis(),
    val timestampDia: String        // Formato "YYYY-MM-DD" para agrupar en stats
)
