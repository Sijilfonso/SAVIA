package com.savia.camaguey.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savia.camaguey.R
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.databinding.ItemCatalogProductBinding
import com.savia.camaguey.util.DateUtils
import com.savia.camaguey.util.PriceFormatter

/**
 * StoreCatalogAdapter: Catálogo de productos/servicios dentro del perfil de tienda.
 * Similar a ProductAdapter pero sin nombre de tienda (ya estamos en ella).
 */
class StoreCatalogAdapter(
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, StoreCatalogAdapter.CatalogViewHolder>(CatalogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val binding = ItemCatalogProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CatalogViewHolder(
        private val binding: ItemCatalogProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.nombre
            binding.tvDescription.text = product.descripcion ?: ""
            binding.tvDescription.visibility = if (product.descripcion.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE

            binding.tvPrice.text = when {
                product.ofertaFlash && product.precioOfertaCUP != null -> {
                    PriceFormatter.formatWithOffer(product.precioCUP, product.precioOfertaCUP, product.monedaMostrar)
                }
                else -> PriceFormatter.format(product.precioCUP, product.monedaMostrar)
            }

            // Badge tipo
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

            // Botón añadir
            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product)
            }
        }
    }

    class CatalogDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }
}
