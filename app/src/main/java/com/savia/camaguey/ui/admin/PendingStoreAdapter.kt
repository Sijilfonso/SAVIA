package com.savia.camaguey.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.databinding.ItemPendingStoreBinding
import com.savia.camaguey.util.Constants

/**
 * PendingStoreAdapter: Lista de negocios pendientes de verificación (admin).
 * Muestra nombre, tipo (MIPYME/TCP/PDL), ID interno, y botones aprobar/rechazar.
 */
class PendingStoreAdapter(
    private val onApprove: (Store) -> Unit,
    private val onReject: (Store) -> Unit
) : ListAdapter<Store, PendingStoreAdapter.PendingViewHolder>(PendingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val binding = ItemPendingStoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PendingViewHolder(
        private val binding: ItemPendingStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Store) {
            binding.tvStoreName.text = store.nombrePublico
            binding.tvStoreType.text = Constants.entityTypeToTechnicalName(store.tipoEntidad)
            binding.tvStoreId.text = store.idInterno
            binding.tvRepresentante.text = store.representanteNombre
            binding.tvDireccion.text = store.direccionCompleta

            binding.btnApprove.setOnClickListener { onApprove(store) }
            binding.btnReject.setOnClickListener { onReject(store) }
        }
    }

    class PendingDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean =
            oldItem == newItem
    }
}
