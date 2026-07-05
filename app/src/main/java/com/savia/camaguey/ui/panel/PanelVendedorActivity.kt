package com.savia.camaguey.ui.panel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.data.repository.AuthRepository
import com.savia.camaguey.data.repository.ProductRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityPanelVendedorBinding
import com.savia.camaguey.ui.login.LoginUnificadoActivity
import com.savia.camaguey.ui.store.StoreCatalogAdapter
import com.savia.camaguey.util.Constants
import com.savia.camaguey.util.DateUtils
import com.savia.camaguey.util.PasswordValidator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PanelVendedorActivity: Dashboard del vendedor con 3 tabs.
 * Tab Resumen: stats de visitas, banner stock, filtros temporales.
 * Tab Catálogo: CRUD productos, oferta flash toggle.
 * Tab Config: ID CMP-XXXX, cambiar contraseña, suscripción, logout.
 */
class PanelVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelVendedorBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var catalogAdapter: StoreCatalogAdapter

    private var storeId: String = ""
    private var currentFilter: String = "today"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verify session
        val prefs = getSharedPreferences("savia_session", MODE_PRIVATE)
        val username = prefs.getString("session_username", null)
        val rol = prefs.getString("session_rol", null)
        if (username == null || rol != "vendedor") {
            startActivity(Intent(this, LoginUnificadoActivity::class.java))
            finish()
            return
        }

        val database = SaviaDatabase.getInstance(this)
        storeRepository = StoreRepository(database)
        productRepository = ProductRepository(database)

        lifecycleScope.launch {
            val store = storeRepository.getByUsername(username)
            if (store != null) {
                storeId = store.id
                binding.tvStoreId.text = getString(R.string.panel_id_label, store.idInterno)
                binding.tvConfigId.text = store.idInterno
                loadData()
            } else {
                Toast.makeText(this@PanelVendedorActivity, "Tienda no encontrada", Toast.LENGTH_LONG).show()
                logout()
            }
        }

        setupTabs()
        setupStatsFilters()
        setupCatalogTab()
        setupConfigTab()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showTab(binding.tabResumen)
                    1 -> showTab(binding.tabCatalogo)
                    2 -> showTab(binding.tabConfig)
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun showTab(view: android.view.View) {
        binding.tabResumen.visibility = android.view.View.GONE
        binding.tabCatalogo.visibility = android.view.View.GONE
        binding.tabConfig.visibility = android.view.View.GONE
        view.visibility = android.view.View.VISIBLE
    }

    private fun setupStatsFilters() {
        val chips = mapOf(
            binding.chipHoy to "today",
            binding.chipSemana to "week",
            binding.chipMes to "month",
            binding.chipAno to "year"
        )

        chips.forEach { (chip, filter) ->
            chip.setOnClickListener {
                currentFilter = filter
                updateChipSelection(chip, chips.keys)
                loadStats()
            }
        }

        // Select first by default
        updateChipSelection(binding.chipHoy, chips.keys)
    }

    private fun updateChipSelection(selected: Chip, all: Set<Chip>) {
        all.forEach {
            if (it == selected) {
                it.setChipBackgroundColorResource(R.color.savia_green_dark)
                it.setTextColor(resources.getColor(R.color.savia_white))
            } else {
                it.setChipBackgroundColorResource(R.color.savia_bg_card)
                it.setTextColor(resources.getColor(R.color.savia_text_primary))
            }
        }
    }

    private fun loadData() {
        loadStats()
        loadCatalog()
        setupStockBanner()
    }

    private fun setupStockBanner() {
        lifecycleScope.launch {
            try {
                val products = productRepository.getByStoreList(storeId)
                val oldest = products.filter { it.tipoItem == "producto" }.minByOrNull { it.ultimaActualizacion ?: Long.MAX_VALUE }
                val daysSince = oldest?.let { DateUtils.daysSince(it.ultimaActualizacion) } ?: 0

                if (daysSince > Constants.STOCK_MARGIN_DAYS) {
                    binding.tvStockBanner.text = getString(R.string.panel_stock_days, daysSince)
                    binding.cardStockBanner.visibility = android.view.View.VISIBLE
                } else {
                    binding.tvStockBanner.text = getString(R.string.panel_stock_banner)
                    binding.cardStockBanner.visibility = android.view.View.VISIBLE
                }

                binding.btnConfirmStock.setOnClickListener {
                    lifecycleScope.launch {
                        productRepository.updateAllStockTimestamp(storeId)
                        binding.tvStockBanner.text = getString(R.string.panel_stock_banner)
                        Toast.makeText(this@PanelVendedorActivity, "Stock confirmado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            try {
                val database = SaviaDatabase.getInstance(this@PanelVendedorActivity)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val now = Date()
                val today = sdf.format(now)
                val weekStart = sdf.format(Date(now.time - 7 * 24 * 60 * 60 * 1000))
                val monthStart = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(now)
                val yearStart = SimpleDateFormat("yyyy", Locale.getDefault()).format(now)

                val (desde, periodo) = when (currentFilter) {
                    "today" -> Pair(today, "day")
                    "week" -> Pair(weekStart, "week")
                    "month" -> Pair(monthStart, "month")
                    "year" -> Pair(yearStart, "year")
                    else -> Pair(today, "day")
                }

                val visitasPerfil = when (periodo) {
                    "day" -> database.visitStatsDao().sumByStoreTypeAndDay(storeId, "perfil", today) ?: 0
                    "week" -> database.visitStatsDao().sumByStoreTypeSince(storeId, "perfil", desde) ?: 0
                    "month" -> database.visitStatsDao().sumByStoreTypeAndMonth(storeId, "perfil", desde) ?: 0
                    "year" -> database.visitStatsDao().sumByStoreTypeAndYear(storeId, "perfil", desde) ?: 0
                    else -> 0
                }

                val visitasProductos = when (periodo) {
                    "day" -> database.visitStatsDao().sumByStoreTypeAndDay(storeId, "producto", today) ?: 0
                    "week" -> database.visitStatsDao().sumByStoreTypeSince(storeId, "producto", desde) ?: 0
                    "month" -> database.visitStatsDao().sumByStoreTypeAndMonth(storeId, "producto", desde) ?: 0
                    "year" -> database.visitStatsDao().sumByStoreTypeAndYear(storeId, "producto", desde) ?: 0
                    else -> 0
                }

                binding.tvVisitsProfile.text = visitasPerfil.toString()
                binding.tvVisitsProducts.text = visitasProductos.toString()
                binding.tvClicksWhatsApp.text = "0" // TODO: track clicks

                val activeCount = productRepository.getByStoreList(storeId).count { it.estadoStock != "agotado" }
                binding.tvProductsActive.text = activeCount.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupCatalogTab() {
        binding.rvCatalogPanel.layoutManager = LinearLayoutManager(this)
        catalogAdapter = StoreCatalogAdapter(
            onAddToCart = { product ->
                toggleFlashOffer(product)
            }
        )
        binding.rvCatalogPanel.adapter = catalogAdapter

        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun loadCatalog() {
        lifecycleScope.launch {
            try {
                val products = productRepository.getByStoreList(storeId)
                catalogAdapter.submitList(products)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toggleFlashOffer(product: Product) {
        lifecycleScope.launch {
            try {
                val updated = product.copy(ofertaFlash = !product.ofertaFlash)
                productRepository.update(updated)
                loadCatalog()
                Toast.makeText(
                    this@PanelVendedorActivity,
                    if (updated.ofertaFlash) "Oferta flash activada" else "Oferta flash desactivada",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showAddProductDialog() {
        AlertDialog.Builder(this)
            .setTitle("Añadir producto")
            .setMessage("Esta función estará disponible en la siguiente versión. Usa el panel web para añadir productos en masa.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupConfigTab() {
        binding.cardChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.cardSubscription.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Pagar suscripción")
                .setMessage("Contacta al soporte de SAVIA para activar tu plan Destacado ($39/mes).")
                .setPositiveButton("Contactar") { _, _ ->
                    // TODO: open WhatsApp with support
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun showChangePasswordDialog() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }
        val etCurrent = android.widget.EditText(this).apply {
            hint = "Contraseña actual"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etNew = android.widget.EditText(this).apply {
            hint = "Nueva contraseña (mín. 8)"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        layout.addView(etCurrent)
        layout.addView(etNew)

        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val current = etCurrent.text.toString()
                val newPass = etNew.text.toString()
                if (newPass.length < Constants.VENDOR_PASSWORD_MIN_LENGTH) {
                    Toast.makeText(this, "Mínimo ${Constants.VENDOR_PASSWORD_MIN_LENGTH} caracteres", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    try {
                        val database = SaviaDatabase.getInstance(this@PanelVendedorActivity)
                        val prefs = getSharedPreferences("savia_session", MODE_PRIVATE)
                        val username = prefs.getString("session_username", "") ?: ""
                        val store = database.storeDao().getByUsername(username) ?: return@launch

                        val valid = PasswordValidator.verifyPassword(current, store.passwordHash)
                        if (!valid) {
                            Toast.makeText(this@PanelVendedorActivity, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val newHash = PasswordValidator.hashPassword(newPass)
                        database.storeDao().updatePassword(store.id, newHash)
                        Toast.makeText(this@PanelVendedorActivity, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        getSharedPreferences("savia_session", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, LoginUnificadoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
