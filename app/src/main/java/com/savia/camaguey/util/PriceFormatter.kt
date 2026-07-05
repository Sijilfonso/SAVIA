package com.savia.camaguey.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utilidad: PriceFormatter
 * Formatea precios en CUP, USD, MLC con símbolos apropiados.
 * Compatible API 21: usa DecimalFormat básico (sin NumberFormat.getCurrencyInstance).
 */
object PriceFormatter {

    private val cupSymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        decimalSeparator = ','
        groupingSeparator = '.'
    }

    private val cupFormat = DecimalFormat("#,##0.00", cupSymbols)
    private val usdFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
    private val simpleFormat = DecimalFormat("#,##0", cupSymbols)

    /**
     * Formatea precio con símbolo de moneda.
     * CUP: $ 1.250,00
     * USD: USD 25.00
     * MLC: MLC 18.50
     */
    fun format(price: Double?, moneda: String): String {
        if (price == null || price <= 0) return "Consultar"

        return when (moneda.uppercase()) {
            "CUP" -> "${Constants.currencySymbol("CUP")} ${cupFormat.format(price)}"
            "USD" -> "${Constants.currencySymbol("USD")} ${usdFormat.format(price)}"
            "MLC" -> "${Constants.currencySymbol("MLC")} ${usdFormat.format(price)}"
            else -> "$price $moneda"
        }
    }

    /**
     * Formatea precio sin símbolo (solo número).
     */
    fun formatNumber(price: Double?): String {
        if (price == null || price <= 0) return "-"
        return cupFormat.format(price)
    }

    /**
     * Formatea precio para mostrar precio original + precio oferta.
     * Ej: "$ 1.250,00 → $ 999,00"
     */
    fun formatWithOffer(price: Double?, offerPrice: Double?, moneda: String): String {
        if (price == null || price <= 0) return "Consultar"
        if (offerPrice == null || offerPrice <= 0) return format(price, moneda)

        val original = format(price, moneda)
        val oferta = format(offerPrice, moneda)
        return "$original → $oferta"
    }

    /**
     * Precio simple para listas: sin decimales si es entero.
     */
    fun formatSimple(price: Double?, moneda: String): String {
        if (price == null || price <= 0) return "Consultar"
        val isInteger = price == price.toInt().toDouble()
        return if (isInteger) {
            "${Constants.currencySymbol(moneda)} ${simpleFormat.format(price)}"
        } else {
            format(price, moneda)
        }
    }

    /**
     * Descuento porcentaje entre precio original y oferta.
     */
    fun discountPercent(original: Double?, offer: Double?): Int? {
        if (original == null || offer == null || original <= 0 || offer <= 0) return null
        if (offer >= original) return null
        return ((1 - offer / original) * 100).toInt().coerceIn(1, 99)
    }
}
