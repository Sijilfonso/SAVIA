package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.VisitStats
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitStatsDao {
    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil' ORDER BY timestamp DESC")
    fun getProfileVisits(tiendaId: String): Flow<List<VisitStats>>

    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto' ORDER BY timestamp DESC")
    fun getProductVisits(tiendaId: String): Flow<List<VisitStats>>

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil' AND fecha = :fecha")
    suspend fun countProfileVisitsToday(tiendaId: String, fecha: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto' AND fecha = :fecha")
    suspend fun countProductVisitsToday(tiendaId: String, fecha: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'perfil'")
    suspend fun countTotalProfileVisits(tiendaId: String): Int

    @Query("SELECT COUNT(*) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = 'producto'")
    suspend fun countTotalProductVisits(tiendaId: String): Int

    @Insert
    suspend fun insert(stat: VisitStats)

    @Query("DELETE FROM visit_stats WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}
