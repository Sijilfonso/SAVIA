package com.savia.camaguey.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.CartItem
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.data.repository.CartRepository
import com.savia.camaguey.data.repository.ProductRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityHomeBinding
import com.savia.camaguey.ui.base.BaseActivity
import com.savia.camaguey.ui.store.StoreProfileActivity
import com.savia.camaguey.util.Constants
import kotlinx.coroutines.launch

/**
 * HomeActivity: Pantalla principal del comprador.
 * SearchView + filtros horizontales (ChipGroup) + negocios destacados (Horizontal RV) + productos (Vertical RV).
 * Extends BaseActivity para BottomNavigation.
 */
class HomeActivity : BaseActivity() {

    override val navItemId: Int = R.id.nav_home

    private lateinit var binding: ActivityHomeBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var productAdapter: ProductAdapter
    private lateinit var featuredAdapter: StoreFeaturedAdapter

    private var currentFilter: String = "all"
    private var currentQuery: String = ""

    private var storesList: List<com.savia.camaguey.data.model.Store> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SaviaDatabase.getInstance(this)
        storeRepository = StoreRepository(database)
        productRepository = ProductRepository(database)
        cartRepository = CartRepository(database)

        setupBottomNavigation(binding.bottomNav)
        setupFeaturedStores()
        setupFilters()
        setupProductList()
        setupSearch()
        loadData()
    }

    private fun setupFeaturedStores() {
        featuredAdapter = StoreFeaturedAdapter { storeId ->
            openStoreProfile(storeId)
        }
        binding.rvFeaturedStores.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFeaturedStores.adapter = featuredAdapter
    }

    private fun setupFilters() {
        val filters = listOf(
            "all" to "Todo",
            "cup" to "CUP",
            "usd" to "USD",
            "today" to "Hoy",
            "deals" to "Gangas",
            "services" to "Servicios"
        )

        filters.forEach { (key, label) ->
            val chip = Chip(this).apply {
                text = label
                isCheckable = true
                isChecked = key == "all"
                setChipBackgroundColorResource(R.color.savia_bg_card)
                setTextColor(resources.getColor(R.color.savia_text_primary))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.savia_border)
                setOnClickListener {
                    currentFilter = key
                    updateFilterChips(key)
                    loadProducts()
                }
            }
            binding.chipGroupFilters.addView(chip)
        }
    }

    private fun updateFilterChips(selectedKey: String) {
        for (i in 0 until binding.chipGroupFilters.childCount) {
            val chip = binding.chipGroupFilters.getChildAt(i) as Chip
            val isSelected = chip.text == when (selectedKey) {
                "all" -> "Todo"
                "cup" -> "CUP"
                "usd" -> "USD"
                "today" -> "Hoy"
                "deals" -> "Gangas"
                "services" -> "Servicios"
                else -> ""
            }
            chip.isChecked = isSelected
            if (isSelected) {
                chip.setChipBackgroundColorResource(R.color.savia_green_dark)
                chip.setTextColor(resources.getColor(R.color.savia_white))
            } else {
                chip.setChipBackgroundColorResource(R.color.savia_bg_card)
                chip.setTextColor(resources.getColor(R.color.savia_text_primary))
            }
        }
    }

    private fun setupProductList() {
        productAdapter = ProductAdapter(
            storesMap = emptyMap(),
            onItemClick = { productId, storeId ->
                openStoreProfile(storeId)
            },
            onAddToCart = { product ->
                addToCart(product)
            }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = productAdapter
        binding.rvProducts.setHasFixedSize(true)
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query ?: ""
                loadProducts()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                if (currentQuery.length >= 2) {
                    loadProducts()
                } else if (currentQuery.isEmpty()) {
                    loadProducts()
                }
                return true
            }
        })
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                storesList = storeRepository.getAllApprovedList()
                featuredAdapter.submitList(storesList.filter { it.planDestacado })

                val storesMap = storesList.associateBy { it.id }
                productAdapter = ProductAdapter(
                    storesMap = storesMap,
                    onItemClick = { productId, storeId ->
                        openStoreProfile(storeId)
                    },
                    onAddToCart = { product ->
                        addToCart(product)
                    }
                )
                binding.rvProducts.adapter = productAdapter

                loadProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                val products = when (currentFilter) {
                    "services" -> productRepository.getAvailableServices()
                    "deals" -> productRepository.getFlashOffers()
                    else -> productRepository.getAllAvailable()
                }

                val filtered = if (currentQuery.isNotEmpty()) {
                    products.filter {
                        it.nombre.contains(currentQuery, ignoreCase = true) ||
                        it.descripcion?.contains(currentQuery, ignoreCase = true) == true
                    }
                } else products

                // Filtro moneda
                val currencyFiltered = when (currentFilter) {
                    "cup" -> filtered.filter { it.precioCUP != null && it.precioCUP > 0 }
                    "usd" -> filtered.filter { it.precioUSD != null && it.precioUSD > 0 }
                    else -> filtered
                }

                productAdapter.submitList(currencyFiltered)

                binding.tvEmpty.visibility = if (currencyFiltered.isEmpty()) View.VISIBLE else View.GONE
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }

    private fun openStoreProfile(storeId: String) {
        val intent = Intent(this, StoreProfileActivity::class.java).apply {
            putExtra(StoreProfileActivity.EXTRA_STORE_ID, storeId)
        }
        startActivity(intent)
    }

    /**
     * Añade un producto al carrito local (Room).
     * FASE 3: Carrito 100% offline.
     */
    private fun addToCart(product: Product) {
        lifecycleScope.launch {
            try {
                val store = storeRepository.getById(product.tiendaId)
                val storeName = store?.nombrePublico ?: "Negocio"
                val item = CartItem(
                    productoId = product.id,
                    tiendaId = product.tiendaId,
                    nombreProducto = product.nombre,
                    precioUnitario = when (product.monedaMostrar.uppercase()) {
                        "USD" -> product.precioUSD ?: product.precioCUP ?: 0.0
                        "MLC" -> product.precioMLC ?: product.precioCUP ?: 0.0
                        else -> product.precioCUP ?: 0.0
                    },
                    moneda = product.monedaMostrar.uppercase(),
                    cantidad = 1
                )
                cartRepository.addItem(item)
                Toast.makeText(this@HomeActivity, "${product.nombre} añadido", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HomeActivity, "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
