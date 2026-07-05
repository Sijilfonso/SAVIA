package com.savia.camaguey.data.repository

import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.AdminAccount
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.util.PasswordValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository: AuthRepository
 * Login unificado para vendedores y admins.
 * Valida username + password contra Store (vendedores) o AdminAccount (admins).
 * Retorna rol: "vendedor" | "admin"
 */
class AuthRepository(private val database: SaviaDatabase) {

    sealed class AuthResult {
        data class Success(val rol: String, val userId: String, val nombre: String) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun login(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        // 1. Buscar en vendedores (stores)
        val store = database.storeDao().getByUsername(username)
        if (store != null) {
            val valid = PasswordValidator.verifyPassword(password, store.passwordHash)
            if (valid) {
                return@withContext AuthResult.Success(
                    rol = store.rol,
                    userId = store.id,
                    nombre = store.nombrePublico
                )
            }
        }

        // 2. Buscar en admins
        val admin = database.adminAccountDao().getByUsername(username)
        if (admin != null) {
            val valid = PasswordValidator.verifyPasswordAdmin(password, admin.passwordHash)
            if (valid) {
                return@withContext AuthResult.Success(
                    rol = admin.rol,
                    userId = admin.id,
                    nombre = admin.nombre
                )
            }
        }

        return@withContext AuthResult.Error("Usuario o contraseña incorrectos")
    }

    suspend fun getStoreByUsername(username: String): Store? =
        database.storeDao().getByUsername(username)

    suspend fun getAdminByUsername(username: String): AdminAccount? =
        database.adminAccountDao().getByUsername(username)

    /**
     * Recuperación de contraseña: genera código de 6 dígitos y lo guarda.
     * En producción, el backend enviaría vía WhatsApp Business API.
     */
    suspend fun generatePasswordReset(username: String): String? = withContext(Dispatchers.IO) {
        // Buscar teléfono de recuperación
        val store = database.storeDao().getByUsername(username)
        val admin = if (store == null) database.adminAccountDao().getByUsername(username) else null

        val telefono = store?.telefonoRecuperacion ?: admin?.telefonoRecuperacion
        if (telefono == null) return@withContext null

        // Generar código 6 dígitos
        val codigo = (100000..999999).random().toString()

        // Guardar en tabla
        val reset = com.savia.camaguey.data.model.PasswordReset(
            username = username,
            codigo = codigo,
            telefonoDestino = telefono
        )
        database.passwordResetDao().insert(reset)
        codigo
    }

    suspend fun verifyResetCode(username: String, codigo: String): Boolean = withContext(Dispatchers.IO) {
        val reset = database.passwordResetDao().getValidByCode(codigo)
        reset != null && reset.username == username
    }
}
