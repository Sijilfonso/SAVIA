package com.savia.camaguey.util

object Constants {
    const val DATABASE_NAME = "savia_database"
    const val DATABASE_VERSION = 1
    const val BASE_URL = "https://api.savia.camaguey.cu/"
    const val DEFAULT_LAT = 21.3839
    const val DEFAULT_LNG = -77.9072
    const val STOCK_PENALTY_DAYS = 30
    const val TRIAL_DAYS = 30
    const val HIDE_AFTER_TRIAL_HOURS = 48
    const val VENDOR_PASSWORD_MIN_LENGTH = 8
    const val ADMIN_PASSWORD_MIN_LENGTH = 12
    const val TYPE_MIPYME = "MIPYME"
    const val TYPE_TCP = "TCP"
    const val TYPE_PDL = "PDL"
    const val ITEM_PRODUCT = "producto"
    const val ITEM_SERVICE = "servicio"
    const val CURRENCY_CUP = "CUP"
    const val CURRENCY_USD = "USD"
    const val PLAN_BASIC = "basico"
    const val PLAN_FEATURED = "destacado"
    const val RESET_CODE_EXPIRY_MINUTES = 15
    const val RESET_CODE_LENGTH = 6

    fun translateEntityType(type: String): String {
        return when (type) {
            TYPE_MIPYME -> "Empresa local"
            TYPE_TCP -> "Negocio personal"
            TYPE_PDL -> "Proyecto comunitario"
            else -> "Negocio"
        }
    }
}
