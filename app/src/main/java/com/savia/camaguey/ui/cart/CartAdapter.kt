package com.savia.camaguey.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savia.camaguey.databinding.ItemCartHeaderBinding
import com.savia.camaguey.databinding.ItemCartProductBinding
import com.savia.camaguey.util.PriceFormatter

/**
 * CartAdapter: RecyclerView con headers de tienda + items del carrito.
 * Maneja CartListItem sealed class: Header + Item.
 */
class CartAdapter(
    private val onIncrement: (com.savia.camaguey.data.model.CartItem) -> Unit,
    private val onDecrement: (com.savia.camaguey.data.model.CartItem) -> Unit,
    private val onRemove: (com.savia.camaguey.data.model.CartItem) -> Unit
) : ListAdapter<CartListItem, RecyclerView.ViewHolder>(CartDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CartListItem.Header -> TYPE_HEADER
            is CartListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemCartHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemCartProductBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CartListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is CartListItem.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemCartHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: CartListItem.Header) {
            binding.tvStoreName.text = header.storeName
        }
    }

    inner class ItemViewHolder(
        private val binding: ItemCartProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartListItem.Item) {
            val cartItem = item.cartItem
            binding.tvProductName.text = cartItem.nombreProducto
            binding.tvPriceUnit.text = PriceFormatter.format(cartItem.precioUnitario, cartItem.moneda)
            binding.tvQuantity.text = cartItem.cantidad.toString()
            binding.tvSubtotal.text = PriceFormatter.format(cartItem.precioUnitario * cartItem.cantidad, cartItem.moneda)

            binding.btnPlus.setOnClickListener { onIncrement(cartItem) }
            binding.btnMinus.setOnClickListener { onDecrement(cartItem) }
            binding.btnRemove.setOnClickListener { onRemove(cartItem) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartListItem>() {
        override fun areItemsTheSame(oldItem: CartListItem, newItem: CartListItem): Boolean {
            return when {
                oldItem is CartListItem.Header && newItem is CartListItem.Header ->
                    oldItem.storeName == newItem.storeName
                oldItem is CartListItem.Item && newItem is CartListItem.Item ->
                    oldItem.cartItem.id == newItem.cartItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CartListItem, newItem: CartListItem): Boolean {
            return oldItem == newItem
        }
    }
}
