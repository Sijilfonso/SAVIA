package com.savia.camaguey.util

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Utilidad: PasswordValidator
 * Valida y verifica contraseñas usando BCrypt.
 * Admin: min 12 chars, mayúscula, minúscula, número, símbolo.
 * Vendedor: min 8 chars, mayúscula, minúscula, número.
 */
object PasswordValidator {

    // Admin: BCrypt cost 12
    private val adminHasher = BCrypt.withDefaults()
    // Vendor: BCrypt cost 10
    private val vendorHasher = BCrypt.withDefaults()

    fun hashPassword(password: String): String {
        return vendorHasher.hashToString(10, password.toCharArray())
    }

    fun hashPasswordAdmin(password: String): String {
        return adminHasher.hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }

    fun verifyPasswordAdmin(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }

    /**
     * Valida contraseña de vendedor (mínimo 8, mayúscula, minúscula, número).
     * @return Pair(isValid, errorMessage)
     */
    fun validateVendor(password: String): Pair<Boolean, String?> {
        if (password.length < 8) return Pair(false, "Mínimo 8 caracteres")
        if (!password.any { it.isUpperCase() }) return Pair(false, "Al menos una mayúscula")
        if (!password.any { it.isLowerCase() }) return Pair(false, "Al menos una minúscula")
        if (!password.any { it.isDigit() }) return Pair(false, "Al menos un número")
        return Pair(true, null)
    }

    /**
     * Valida contraseña de admin (mínimo 12, mayúscula, minúscula, número, símbolo).
     * @return Pair(isValid, errorMessage)
     */
    fun validateAdmin(password: String): Pair<Boolean, String?> {
        if (password.length < 12) return Pair(false, "Mínimo 12 caracteres")
        if (!password.any { it.isUpperCase() }) return Pair(false, "Al menos una mayúscula")
        if (!password.any { it.isLowerCase() }) return Pair(false, "Al menos una minúscula")
        if (!password.any { it.isDigit() }) return Pair(false, "Al menos un número")
        if (!password.any { !it.isLetterOrDigit() }) return Pair(false, "Al menos un símbolo especial")
        return Pair(true, null)
    }

    /**
     * Valida username (solo letras, números, puntos, guiones bajos).
     */
    fun validateUsername(username: String): Pair<Boolean, String?> {
        if (username.length < 3) return Pair(false, "Mínimo 3 caracteres")
        if (!username.matches(Regex("^[a-zA-Z0-9._]+$"))) return Pair(false, "Solo letras, números, puntos y guiones bajos")
        return Pair(true, null)
    }
}
