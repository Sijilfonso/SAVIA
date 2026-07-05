package com.savia.camaguey.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.databinding.ItemProductBinding
import com.savia.camaguey.util.DateUtils
import com.savia.camaguey.util.PriceFormatter

/**
 * ProductAdapter: Lista vertical de productos/servicios en Home.
 * ViewHolder optimizado, sin animaciones pesadas (gama baja).
 * Compatible API 21: usa ContextCompat.getColor().
 */
class ProductAdapter(
    private val storesMap: Map<String, com.savia.camaguey.data.model.Store> = emptyMap(),
    private val onItemClick: (productId: String, storeId: String) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.nombre
            binding.tvStoreName.text = storesMap[product.tiendaId]?.nombrePublico ?: "Negocio"

            binding.tvPrice.text = when {
                product.ofertaFlash && product.precioOfertaCUP != null -> {
                    PriceFormatter.formatWithOffer(product.precioCUP, product.precioOfertaCUP, product.monedaMostrar)
                }
                else -> PriceFormatter.format(product.precioCUP, product.monedaMostrar)
            }

            // Badge tipo item
            binding.tvBadge.text = if (product.tipoItem == "servicio") "Servicio" else "Producto"
            val ctx = itemView.context
            val badgeColor = if (product.tipoItem == "servicio")
                ContextCompat.getColor(ctx, android.R.color.holo_blue_dark)
            else
                ContextCompat.getColor(ctx, android.R.color.holo_green_dark)
            binding.tvBadge.setBackgroundColor(badgeColor)

            // Stock badge
            val daysSinceUpdate = DateUtils.daysSince(product.ultimaActualizacion)
            val stockLabel = when {
                product.estadoStock == "agotado" -> "Agotado"
                product.tipoItem == "servicio" -> "Disponible"
                daysSinceUpdate <= 30 -> "En stock"
                else -> "Stock antiguo"
            }
            binding.tvStockBadge.text = stockLabel
            val stockColor = when {
                product.estadoStock == "agotado" -> ContextCompat.getColor(ctx, android.R.color.holo_red_dark)
                product.tipoItem == "servicio" -> ContextCompat.getColor(ctx, android.R.color.holo_blue_dark)
                daysSinceUpdate <= 30 -> ContextCompat.getColor(ctx, android.R.color.holo_green_dark)
                else -> ContextCompat.getColor(ctx, android.R.color.holo_orange_dark)
            }
            binding.tvStockBadge.setBackgroundColor(stockColor)
            binding.tvStockBadge.setTextColor(ContextCompat.getColor(ctx, R.color.savia_white))

            // Click en card
            binding.root.setOnClickListener {
                onItemClick(product.id, product.tiendaId)
            }

            // Botón añadir al carrito
            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }
}
