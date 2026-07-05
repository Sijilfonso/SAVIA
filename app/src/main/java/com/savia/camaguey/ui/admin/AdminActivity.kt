package com.savia.camaguey.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.data.repository.ProductRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityAdminBinding
import com.savia.camaguey.ui.login.LoginUnificadoActivity
import com.savia.camaguey.util.Constants
import kotlinx.coroutines.launch

/**
 * AdminActivity: Panel de administración con 3 tabs.
 * Tab Verificación: lista de negocios pendientes (aprobar/rechazar).
 * Tab Métricas: stats generales de la plataforma.
 * Tab Moderación: revisar ofertas flash.
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verify session
        val prefs = getSharedPreferences("savia_session", MODE_PRIVATE)
        val rol = prefs.getString("session_rol", null)
        if (rol != "admin") {
            startActivity(Intent(this, LoginUnificadoActivity::class.java))
            finish()
            return
        }

        val database = SaviaDatabase.getInstance(this)
        storeRepository = StoreRepository(database)
        productRepository = ProductRepository(database)

        setupTabs()
        loadData()

        binding.btnLogoutAdmin.setOnClickListener { logout() }
    }

    private fun setupTabs() {
        binding.tabLayoutAdmin.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showTab(binding.tabVerificacion)
                    1 -> showTab(binding.tabMetrics)
                    2 -> showTab(binding.tabModeration)
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun showTab(view: android.view.View) {
        binding.tabVerificacion.visibility = android.view.View.GONE
        binding.tabMetrics.visibility = android.view.View.GONE
        binding.tabModeration.visibility = android.view.View.GONE
        view.visibility = android.view.View.VISIBLE
    }

    private fun loadData() {
        loadVerificationList()
        loadMetrics()
        loadModeration()
    }

    private fun loadVerificationList() {
        lifecycleScope.launch {
            try {
                val pendingStores = storeRepository.getPendingVerification()
                val adapter = PendingStoreAdapter(
                    onApprove = { store -> approveStore(store) },
                    onReject = { store -> rejectStore(store) }
                )
                binding.rvPendingStores.layoutManager = LinearLayoutManager(this@AdminActivity)
                binding.rvPendingStores.adapter = adapter
                adapter.submitList(pendingStores)

                val approvedCount = storeRepository.getAllApprovedList().size
                binding.tvApprovedStores.text = approvedCount.toString()
                binding.tvPendingCount.text = pendingStores.size.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun approveStore(store: Store) {
        lifecycleScope.launch {
            try {
                val updated = store.copy(
                    verificado = true,
                    estadoVerificacion = "aprobado",
                    suscripcionActiva = true
                )
                storeRepository.update(updated)
                Toast.makeText(this@AdminActivity, "${store.nombrePublico} aprobado", Toast.LENGTH_SHORT).show()
                loadVerificationList()
                loadMetrics()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun rejectStore(store: Store) {
        AlertDialog.Builder(this)
            .setTitle("Rechazar negocio")
            .setMessage("¿Confirmas que deseas rechazar a ${store.nombrePublico}?")
            .setPositiveButton("Rechazar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val updated = store.copy(
                            verificado = false,
                            estadoVerificacion = "rechazado"
                        )
                        storeRepository.update(updated)
                        Toast.makeText(this@AdminActivity, "${store.nombrePublico} rechazado", Toast.LENGTH_SHORT).show()
                        loadVerificationList()
                        loadMetrics()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadMetrics() {
        lifecycleScope.launch {
            try {
                val approvedCount = storeRepository.getAllApprovedList().size
                val pendingCount = storeRepository.getPendingVerification().size
                binding.tvApprovedStores.text = approvedCount.toString()
                binding.tvPendingCount.text = pendingCount.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadModeration() {
        lifecycleScope.launch {
            try {
                val flashOffers = productRepository.getFlashOffers()
                // Display flash offers in moderation tab
                binding.rvFlashOffers.layoutManager = LinearLayoutManager(this@AdminActivity)
                // For MVP, show count or a simple list
                // TODO: implement proper adapter for flash offers moderation
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun logout() {
        getSharedPreferences("savia_session", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, LoginUnificadoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
