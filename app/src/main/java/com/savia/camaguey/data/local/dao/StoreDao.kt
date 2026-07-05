package com.savia.camaguey.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.savia.camaguey.data.model.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores WHERE estadoVerificacion = 'aprobado' ORDER BY nombrePublico ASC")
    fun getAllApproved(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE estadoVerificacion = 'aprobado'")
    suspend fun getAllApprovedList(): List<Store>

    @Query("SELECT * FROM stores WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Store?

    @Query("SELECT * FROM stores WHERE idInterno = :idInterno LIMIT 1")
    suspend fun getByIdInterno(idInterno: String): Store?

    @Query("SELECT * FROM stores WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): Store?

    @Query("SELECT * FROM stores WHERE zona = :zona AND estadoVerificacion = 'aprobado'")
    suspend fun getByZona(zona: String): List<Store>

    @Query("SELECT * FROM stores WHERE categoriaPrincipal = :categoria AND estadoVerificacion = 'aprobado'")
    suspend fun getByCategoria(categoria: String): List<Store>

    @Query("SELECT * FROM stores WHERE planDestacado = 1 AND estadoVerificacion = 'aprobado'")
    suspend fun getFeatured(): List<Store>

    @Query("SELECT * FROM stores WHERE estadoVerificacion = 'pendiente'")
    suspend fun getPendingVerification(): List<Store>

    @Query("SELECT DISTINCT zona FROM stores WHERE estadoVerificacion = 'aprobado' ORDER BY zona ASC")
    suspend fun getAllZonas(): List<String>

    @Query("SELECT DISTINCT categoriaPrincipal FROM stores WHERE estadoVerificacion = 'aprobado' ORDER BY categoriaPrincipal ASC")
    suspend fun getAllCategorias(): List<String>

    @Query("SELECT COUNT(*) FROM stores WHERE estadoVerificacion = 'aprobado'")
    suspend fun countApproved(): Int

    @Query("UPDATE stores SET ultimaActualizacion = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: Store)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stores: List<Store>)

    @Update
    suspend fun update(store: Store)

    @Delete
    suspend fun delete(store: Store)

    @Query("UPDATE stores SET passwordHash = :newHash WHERE id = :id")
    suspend fun updatePassword(id: String, newHash: String)

    @Query("DELETE FROM stores")
    suspend fun deleteAll()
}
