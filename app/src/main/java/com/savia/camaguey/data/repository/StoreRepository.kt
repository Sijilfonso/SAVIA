package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.dao.StoreDao
import com.savia.camaguey.data.model.Store
import kotlinx.coroutines.flow.Flow

class StoreRepository(private val storeDao: StoreDao) {

    fun getAllActive(): Flow<List<Store>> = storeDao.getAllActive()
    fun getFeatured(): Flow<List<Store>> = storeDao.getFeatured()
    fun getByZona(zona: String): Flow<List<Store>> = storeDao.getByZona(zona)
    fun getByCategoria(categoria: String): Flow<List<Store>> = storeDao.getByCategoria(categoria)
    fun getByTipoEntidad(tipo: String): Flow<List<Store>> = storeDao.getByTipoEntidad(tipo)

    suspend fun getById(id: String): Store? = storeDao.getById(id)
    suspend fun getByIdInterno(idInterno: String): Store? = storeDao.getByIdInterno(idInterno)
    suspend fun getByUsername(username: String): Store? = storeDao.getByUsername(username)

    suspend fun insert(store: Store) = storeDao.insert(store)
    suspend fun insertAll(stores: List<Store>) = storeDao.insertAll(stores)
    suspend fun update(store: Store) = storeDao.update(store)
    suspend fun delete(store: Store) = storeDao.delete(store)

    suspend fun confirmStock(storeId: String) {
        storeDao.updateStockConfirmation(storeId, System.currentTimeMillis())
    }

    suspend fun count(): Int = storeDao.count()
}
