package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.savia.camaguey.data.model.VisitStats

@Dao
interface VisitStatsDao {
    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia = :dia LIMIT 1")
    suspend fun getByStoreTypeAndDay(tiendaId: String, tipo: String, dia: String): VisitStats?

    @Query("SELECT SUM(conteo) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia = :dia")
    suspend fun sumByStoreTypeAndDay(tiendaId: String, tipo: String, dia: String): Int?

    @Query("SELECT SUM(conteo) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampDia >= :desde")
    suspend fun sumByStoreTypeSince(tiendaId: String, tipo: String, desde: String): Int?

    @Query("SELECT SUM(conteo) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampSemana = :semana")
    suspend fun sumByStoreTypeAndWeek(tiendaId: String, tipo: String, semana: String): Int?

    @Query("SELECT SUM(conteo) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampMes = :mes")
    suspend fun sumByStoreTypeAndMonth(tiendaId: String, tipo: String, mes: String): Int?

    @Query("SELECT SUM(conteo) FROM visit_stats WHERE tiendaId = :tiendaId AND tipo = :tipo AND timestampAno = :ano")
    suspend fun sumByStoreTypeAndYear(tiendaId: String, tipo: String, ano: String): Int?

    @Query("SELECT * FROM visit_stats WHERE tiendaId = :tiendaId ORDER BY timestampDia DESC LIMIT 30")
    suspend fun getLast30Days(tiendaId: String): List<VisitStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: VisitStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stats: List<VisitStats>)

    @Query("DELETE FROM visit_stats WHERE tiendaId = :tiendaId")
    suspend fun deleteByStore(tiendaId: String)

    @Query("DELETE FROM visit_stats")
    suspend fun deleteAll()
}
