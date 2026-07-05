package com.savia.camaguey.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savia.camaguey.R
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.databinding.ItemStoreFeaturedBinding
import com.savia.camaguey.util.Constants

/**
 * StoreFeaturedAdapter: Lista horizontal de negocios destacados.
 * ViewHolder optimizado, sin animaciones pesadas.
 */
class StoreFeaturedAdapter(
    private val onStoreClick: (storeId: String) -> Unit
) : ListAdapter<Store, StoreFeaturedAdapter.StoreViewHolder>(StoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreFeaturedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoreViewHolder(
        private val binding: ItemStoreFeaturedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Store) {
            binding.tvStoreName.text = store.nombrePublico
            binding.tvStoreCategory.text = store.categoriaPrincipal
            binding.tvStoreZone.text = store.zona
            binding.tvEntityType.text = Constants.entityTypeToDisplayName(store.tipoEntidad)
            val ctx = itemView.context
            val entityColor = when (store.tipoEntidad.uppercase()) {
                "MIPYME" -> ContextCompat.getColor(ctx, android.R.color.holo_blue_dark)
                "TCP" -> ContextCompat.getColor(ctx, android.R.color.holo_green_dark)
                "PDL" -> ContextCompat.getColor(ctx, android.R.color.holo_purple)
                else -> ContextCompat.getColor(ctx, R.color.savia_text_secondary)
            }
            binding.tvEntityType.setBackgroundColor(entityColor)
            binding.tvEntityType.setTextColor(ContextCompat.getColor(ctx, R.color.savia_white))

            // Badge destacado
            if (store.planDestacado) {
                binding.tvFeaturedBadge.visibility = android.view.View.VISIBLE
            } else {
                binding.tvFeaturedBadge.visibility = android.view.View.GONE
            }

            // Badge verificado
            if (store.verificado) {
                binding.tvVerifiedBadge.visibility = android.view.View.VISIBLE
            } else {
                binding.tvVerifiedBadge.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onStoreClick(store.id)
            }
        }
    }

    class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean =
            oldItem == newItem
    }
}
