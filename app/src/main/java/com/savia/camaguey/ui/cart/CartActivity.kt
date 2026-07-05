package com.savia.camaguey.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.CartItem
import com.savia.camaguey.data.repository.CartRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityCartBinding
import com.savia.camaguey.ui.base.BaseActivity
import com.savia.camaguey.ui.route.RouteActivity
import com.savia.camaguey.util.PriceFormatter
import kotlinx.coroutines.launch

/**
 * CartActivity: Carrito de compras agrupado por negocio.
 * RecyclerView con items, cantidad +/-, total por moneda, botón "Ver ruta".
 * 100% offline en Room.
 */
class CartActivity : BaseActivity() {

    override val navItemId: Int = R.id.nav_cart

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var storeRepository: StoreRepository
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNav)

        val database = SaviaDatabase.getInstance(this)
        cartRepository = CartRepository(database)
        storeRepository = StoreRepository(database)

        setupCartList()
        setupActions()
        loadCart()
    }

    private fun setupCartList() {
        cartAdapter = CartAdapter(
            onIncrement = { item ->
                lifecycleScope.launch { cartRepository.incrementQuantity(item.id) }
            },
            onDecrement = { item ->
                lifecycleScope.launch { cartRepository.decrementQuantity(item.id) }
            },
            onRemove = { item ->
                lifecycleScope.launch { cartRepository.deleteItem(item) }
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = cartAdapter
    }

    private fun setupActions() {
        binding.btnClearCart.setOnClickListener {
            lifecycleScope.launch {
                cartRepository.clearCart()
            }
        }

        binding.btnViewRoute.setOnClickListener {
            lifecycleScope.launch {
                val storeIds = cartRepository.getStoresInCart()
                if (storeIds.size >= 2) {
                    val intent = Intent(this@CartActivity, RouteActivity::class.java).apply {
                        putStringArrayListExtra(RouteActivity.EXTRA_STORE_IDS, ArrayList(storeIds))
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadCart() {
        lifecycleScope.launch {
            cartRepository.getAll().collect { items ->
                if (items.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                    binding.layoutTotals.visibility = View.GONE
                    binding.btnViewRoute.visibility = View.GONE
                    cartAdapter.submitList(emptyList())
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    binding.layoutTotals.visibility = View.VISIBLE

                    val storeIds = items.map { it.tiendaId }.distinct()
                    binding.btnViewRoute.visibility = if (storeIds.size >= 2) View.VISIBLE else View.GONE

                    // Group by store
                    val grouped = items.groupBy { it.tiendaId }
                    val storeNames = mutableMapOf<String, String>()
                    storeIds.forEach { id ->
                        storeNames[id] = storeRepository.getById(id)?.nombrePublico ?: "Negocio"
                    }

                    val cartItemsWithHeaders = mutableListOf<CartListItem>()
                    grouped.forEach { (storeId, itemsGroup) ->
                        cartItemsWithHeaders.add(CartListItem.Header(storeNames[storeId] ?: "Negocio"))
                        itemsGroup.forEach { cartItemsWithHeaders.add(CartListItem.Item(it)) }
                    }
                    cartAdapter.submitList(cartItemsWithHeaders)

                    // Totals
                    val totalCUP = cartRepository.getTotalByCurrency("CUP")
                    val totalUSD = cartRepository.getTotalByCurrency("USD")
                    val totalMLC = cartRepository.getTotalByCurrency("MLC")

                    binding.tvTotalCUP.text = if (totalCUP > 0) "CUP: ${PriceFormatter.formatNumber(totalCUP)}" else ""
                    binding.tvTotalCUP.visibility = if (totalCUP > 0) View.VISIBLE else View.GONE
                    binding.tvTotalUSD.text = if (totalUSD > 0) "USD: ${PriceFormatter.formatNumber(totalUSD)}" else ""
                    binding.tvTotalUSD.visibility = if (totalUSD > 0) View.VISIBLE else View.GONE
                    binding.tvTotalMLC.text = if (totalMLC > 0) "MLC: ${PriceFormatter.formatNumber(totalMLC)}" else ""
                    binding.tvTotalMLC.visibility = if (totalMLC > 0) View.VISIBLE else View.GONE
                }
            }
        }
    }
}

sealed class CartListItem {
    data class Header(val storeName: String) : CartListItem()
    data class Item(val cartItem: CartItem) : CartListItem()
}
