package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.dao.ProductDao
import com.savia.camaguey.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getByStore(tiendaId: String): Flow<List<Product>> = productDao.getByStore(tiendaId)
    fun getAllInStock(): Flow<List<Product>> = productDao.getAllInStock()
    fun getAllServices(): Flow<List<Product>> = productDao.getAllServices()
    fun getFlashOffers(): Flow<List<Product>> = productDao.getFlashOffers()
    fun getByCategoria(categoria: String): Flow<List<Product>> = productDao.getByCategoria(categoria)
    fun search(query: String): Flow<List<Product>> = productDao.search(query)
    fun getByCurrency(moneda: String): Flow<List<Product>> = productDao.getByCurrency(moneda)

    suspend fun getById(id: String): Product? = productDao.getById(id)
    suspend fun insert(product: Product) = productDao.insert(product)
    suspend fun insertAll(products: List<Product>) = productDao.insertAll(products)
    suspend fun update(product: Product) = productDao.update(product)
    suspend fun delete(product: Product) = productDao.delete(product)
    suspend fun deleteByStore(tiendaId: String) = productDao.deleteByStore(tiendaId)
    suspend fun countByStore(tiendaId: String): Int = productDao.countByStore(tiendaId)
}
