package com.savia.camaguey.ui.store

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.CartItem
import com.savia.camaguey.data.model.VisitStats
import com.savia.camaguey.data.repository.CartRepository
import com.savia.camaguey.data.repository.ProductRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityStoreProfileBinding
import com.savia.camaguey.util.Constants
import com.savia.camaguey.util.DateUtils
import com.savia.camaguey.util.PriceFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * StoreProfileActivity: Perfil público del negocio (comprador).
 * Header con nombre, badges, info, WhatsApp deep link, dirección copiable, catálogo filtrado.
 * Registra visitas en VisitStats para panel del vendedor.
 * NO muestra ID CMP-XXXX, NO muestra MIPYME/TCP/PDL, NO muestra número teléfono en texto.
 */
class StoreProfileActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORE_ID = "store_id"
    }

    private lateinit var binding: ActivityStoreProfileBinding
    private lateinit var storeRepository: StoreRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private var storeId: String = ""
    private var whatsappNumber: String = ""
    private lateinit var catalogAdapter: StoreCatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeId = intent.getStringExtra(EXTRA_STORE_ID) ?: return finish()

        val database = SaviaDatabase.getInstance(this)
        storeRepository = StoreRepository(database)
        productRepository = ProductRepository(database)
        cartRepository = CartRepository(database)

        setupCatalog()
        loadStore()
        setupBackButton()

        // FASE 3: Registrar visita al perfil del negocio
        registerVisit()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupCatalog() {
        catalogAdapter = StoreCatalogAdapter(
            onAddToCart = { product ->
                addToCart(product)
            }
        )
        binding.rvCatalog.layoutManager = LinearLayoutManager(this)
        binding.rvCatalog.adapter = catalogAdapter

        // Filtros catálogo
        binding.filterAll.setOnClickListener { loadCatalog("all") }
        binding.filterStock.setOnClickListener { loadCatalog("stock") }
        binding.filterOrder.setOnClickListener { loadCatalog("order") }
        binding.filterServices.setOnClickListener { loadCatalog("services") }
    }

    private fun loadStore() {
        lifecycleScope.launch {
            try {
                val store = storeRepository.getById(storeId) ?: return@launch finish()
                whatsappNumber = store.telefonoWhatsApp

                binding.tvStoreName.text = store.nombrePublico
                binding.tvEntityType.text = Constants.entityTypeToDisplayName(store.tipoEntidad)
                binding.tvAddress.text = store.direccionCompleta
                binding.tvZone.text = store.zona
                binding.tvSchedule.text = store.horario
                binding.tvDelivery.text = store.entregaInfo

                // Badges
                binding.tvVerifiedBadge.visibility = if (store.verificado) android.view.View.VISIBLE else android.view.View.GONE
                binding.tvFeaturedBadge.visibility = if (store.planDestacado) android.view.View.VISIBLE else android.view.View.GONE

                // WhatsApp button
                binding.btnWhatsApp.setOnClickListener {
                    openWhatsApp(whatsappNumber)
                }

                // Copy address
                binding.tvAddress.setOnClickListener {
                    copyToClipboard(store.direccionCompleta)
                }

                loadCatalog("all")
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        }
    }

    private fun loadCatalog(filter: String) {
        lifecycleScope.launch {
            try {
                val allProducts = productRepository.getByStoreList(storeId)
                val filtered = when (filter) {
                    "stock" -> allProducts.filter { it.estadoStock == "disponible" && it.tipoItem == "producto" }
                    "order" -> allProducts.filter { it.estadoStock == "por_encargo" }
                    "services" -> allProducts.filter { it.tipoItem == "servicio" }
                    else -> allProducts
                }
                catalogAdapter.submitList(filtered)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * FASE 3: Añade producto/servicio al carrito local (Room).
     */
    private fun addToCart(product: com.savia.camaguey.data.model.Product) {
        lifecycleScope.launch {
            try {
                val price = when (product.monedaMostrar.uppercase()) {
                    "USD" -> product.precioUSD ?: product.precioCUP ?: 0.0
                    "MLC" -> product.precioMLC ?: product.precioCUP ?: 0.0
                    else -> product.precioCUP ?: 0.0
                }
                val item = CartItem(
                    productoId = product.id,
                    tiendaId = product.tiendaId,
                    nombreProducto = product.nombre,
                    precioUnitario = price,
                    moneda = product.monedaMostrar.uppercase(),
                    cantidad = 1
                )
                cartRepository.addItem(item)
                Toast.makeText(this@StoreProfileActivity, "${product.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@StoreProfileActivity, "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * FASE 3: Registra visita al perfil en VisitStats.
     * Solo la tienda dueña ve sus estadísticas en el panel vendedor.
     */
    private fun registerVisit() {
        lifecycleScope.launch {
            try {
                val database = SaviaDatabase.getInstance(this@StoreProfileActivity)
                val now = Date()
                val sdfDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val sdfSemana = SimpleDateFormat("yyyy-'W'ww", Locale.getDefault())
                val sdfMes = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val sdfAno = SimpleDateFormat("yyyy", Locale.getDefault())

                val stats = VisitStats(
                    tiendaId = storeId,
                    tipo = "perfil",
                    timestampDia = sdfDia.format(now),
                    timestampSemana = sdfSemana.format(now),
                    timestampMes = sdfMes.format(now),
                    timestampAno = sdfAno.format(now)
                )
                database.visitStatsDao().insert(stats)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openWhatsApp(phone: String) {
        val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
        val url = "https://wa.me/$cleanPhone"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Dirección", text))
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }
}
