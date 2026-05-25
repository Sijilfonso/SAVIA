package com.savia.camaguey.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {

    private val formatter = NumberFormat.getNumberInstance(Locale("es", "CU"))

    fun format(price: Double, currency: String): String {
        return "\$${formatter.format(price.toInt())} $currency"
    }

    fun formatSimple(price: Double): String {
        return "\$${formatter.format(price.toInt())}"
    }
}
