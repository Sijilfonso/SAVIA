package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.InteractionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionLogDao {
    @Query("SELECT * FROM interaction_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<InteractionLog>>

    @Query("SELECT * FROM interaction_logs WHERE tipo = :tipo ORDER BY timestamp DESC")
    fun getByType(tipo: String): Flow<List<InteractionLog>>

    @Insert
    suspend fun insert(log: InteractionLog)

    @Query("DELETE FROM interaction_logs WHERE timestamp < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}
