package com.savia.camaguey.util

object PasswordValidator {

    fun isValidVendor(password: String): Boolean {
        if (password.length < Constants.VENDOR_PASSWORD_MIN_LENGTH) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUpper && hasLower && hasDigit
    }

    fun isValidAdmin(password: String): Boolean {
        if (password.length < Constants.ADMIN_PASSWORD_MIN_LENGTH) return false
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        return hasUpper && hasLower && hasDigit && hasSpecial
    }

    fun getVendorError(password: String): String? {
        return when {
            password.length < Constants.VENDOR_PASSWORD_MIN_LENGTH ->
                "Mínimo ${Constants.VENDOR_PASSWORD_MIN_LENGTH} caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isLowerCase() } -> "Debe incluir una minúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            else -> null
        }
    }

    fun getAdminError(password: String): String? {
        return when {
            password.length < Constants.ADMIN_PASSWORD_MIN_LENGTH ->
                "Mínimo ${Constants.ADMIN_PASSWORD_MIN_LENGTH} caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isLowerCase() } -> "Debe incluir una minúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            !password.any { !it.isLetterOrDigit() } -> "Debe incluir un símbolo especial"
            else -> null
        }
    }
}
