package com.savia.camaguey.data.local.dao

import androidx.room.*
import com.savia.camaguey.data.model.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores WHERE suscripcionActiva = 1 ORDER BY nombrePublico ASC")
    fun getAllActive(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Store?

    @Query("SELECT * FROM stores WHERE idInterno = :idInterno LIMIT 1")
    suspend fun getByIdInterno(idInterno: String): Store?

    @Query("SELECT * FROM stores WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): Store?

    @Query("SELECT * FROM stores WHERE zona = :zona AND suscripcionActiva = 1")
    fun getByZona(zona: String): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE categoriaPrincipal = :categoria AND suscripcionActiva = 1")
    fun getByCategoria(categoria: String): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE destacado = 1 AND suscripcionActiva = 1")
    fun getFeatured(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE tipoEntidad = :tipo AND suscripcionActiva = 1")
    fun getByTipoEntidad(tipo: String): Flow<List<Store>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: Store)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stores: List<Store>)

    @Update
    suspend fun update(store: Store)

    @Delete
    suspend fun delete(store: Store)

    @Query("UPDATE stores SET ultimaConfirmacionStock = :timestamp WHERE id = :storeId")
    suspend fun updateStockConfirmation(storeId: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun count(): Int
}
