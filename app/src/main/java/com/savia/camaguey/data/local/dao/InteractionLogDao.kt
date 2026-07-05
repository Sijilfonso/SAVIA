package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savia.camaguey.data.model.InteractionLog

@Dao
interface InteractionLogDao {
    @Query("SELECT * FROM interaction_logs WHERE tiendaId = :tiendaId ORDER BY fecha DESC")
    suspend fun getByStore(tiendaId: String): List<InteractionLog>

    @Query("SELECT * FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia = :dia")
    suspend fun getByStoreTypeAndDay(tiendaId: String, tipo: String, dia: String): List<InteractionLog>

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia = :dia")
    suspend fun countByStoreTypeAndDay(tiendaId: String, tipo: String, dia: String): Int

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia >= :desde")
    suspend fun countByStoreTypeSince(tiendaId: String, tipo: String, desde: String): Int

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = 'perfil_visto' AND timestampDia = :dia")
    suspend fun countProfileViewsToday(tiendaId: String, dia: String): Int

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = 'producto_visto' AND timestampDia = :dia")
    suspend fun countProductViewsToday(tiendaId: String, dia: String): Int

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE tiendaId = :tiendaId AND tipo = 'click_whatsapp' AND timestampDia = :dia")
    suspend fun countWhatsAppClicksToday(tiendaId: String, dia: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: InteractionLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<InteractionLog>)

    @Query("DELETE FROM interaction_logs WHERE tiendaId = :tiendaId")
    suspend fun deleteByStore(tiendaId: String)

    @Query("DELETE FROM interaction_logs")
    suspend fun deleteAll()
}
