package com.savia.camaguey.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.repository.AuthRepository
import com.savia.camaguey.databinding.ActivityPasswordRecoveryBinding
import com.savia.camaguey.util.Constants
import com.savia.camaguey.util.PasswordValidator
import kotlinx.coroutines.launch

/**
 * PasswordRecoveryActivity: Flujo de 2 pasos para recuperar contraseña.
 * Paso 1: Input username → generar código 6 dígitos.
 * Paso 2: Input código + nueva contraseña + confirmar.
 * Fallback: wa.me al soporte si no hay WhatsApp Business API.
 */
class PasswordRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordRecoveryBinding
    private lateinit var authRepository: AuthRepository

    private var currentUsername: String = ""
    private var isStepOne: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SaviaDatabase.getInstance(this)
        authRepository = AuthRepository(database)

        showStepOne()

        binding.btnAction.setOnClickListener {
            if (isStepOne) sendCode() else resetPassword()
        }

        binding.tvFallback.setOnClickListener {
            openSupportWhatsApp()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun showStepOne() {
        isStepOne = true
        binding.tvTitle.text = getString(R.string.recovery_title)
        binding.tvInstructions.text = "Ingresa tu nombre de usuario. Te enviaremos un código de 6 dígitos por WhatsApp."
        binding.tilCode.visibility = android.view.View.GONE
        binding.tilNewPassword.visibility = android.view.View.GONE
        binding.tilConfirmPassword.visibility = android.view.View.GONE
        binding.btnAction.text = getString(R.string.recovery_send_code)
        binding.tvFallback.visibility = android.view.View.VISIBLE
    }

    private fun showStepTwo() {
        isStepOne = false
        binding.tvTitle.text = "Restablecer contraseña"
        binding.tvInstructions.text = "Ingresa el código de 6 dígitos que recibiste por WhatsApp y tu nueva contraseña."
        binding.tilCode.visibility = android.view.View.VISIBLE
        binding.tilNewPassword.visibility = android.view.View.VISIBLE
        binding.tilConfirmPassword.visibility = android.view.View.VISIBLE
        binding.btnAction.text = getString(R.string.recovery_button)
        binding.tvFallback.visibility = android.view.View.GONE
    }

    private fun sendCode() {
        currentUsername = binding.etUsername.text.toString().trim()
        if (currentUsername.isEmpty()) {
            binding.etUsername.error = "Ingresa tu usuario"
            return
        }

        binding.btnAction.isEnabled = false
        binding.btnAction.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                val codigo = authRepository.generatePasswordReset(currentUsername)
                if (codigo != null) {
                    // En producción: backend envía por WhatsApp Business API
                    // En MVP: mostramos código en Toast para testing
                    Toast.makeText(
                        this@PasswordRecoveryActivity,
                        "Código generado: $codigo (simulación — en producción llega por WhatsApp)",
                        Toast.LENGTH_LONG
                    ).show()
                    showStepTwo()
                } else {
                    Toast.makeText(
                        this@PasswordRecoveryActivity,
                        "Usuario no encontrado",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.btnAction.isEnabled = true
                binding.btnAction.text = getString(R.string.recovery_send_code)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@PasswordRecoveryActivity, R.string.error_generic, Toast.LENGTH_LONG).show()
                binding.btnAction.isEnabled = true
                binding.btnAction.text = getString(R.string.recovery_send_code)
            }
        }
    }

    private fun resetPassword() {
        val code = binding.etCode.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (code.length != 6) {
            binding.etCode.error = "Código de 6 dígitos"
            return
        }
        if (newPassword.length < Constants.VENDOR_PASSWORD_MIN_LENGTH) {
            binding.etNewPassword.error = "Mínimo ${Constants.VENDOR_PASSWORD_MIN_LENGTH} caracteres"
            return
        }
        if (newPassword != confirmPassword) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            return
        }

        binding.btnAction.isEnabled = false
        binding.btnAction.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                val valid = authRepository.verifyResetCode(currentUsername, code)
                if (valid) {
                    // Actualizar contraseña en Store o AdminAccount
                    val database = SaviaDatabase.getInstance(this@PasswordRecoveryActivity)
                    val store = database.storeDao().getByUsername(currentUsername)
                    if (store != null) {
                        val newHash = PasswordValidator.hashPassword(newPassword)
                        database.storeDao().updatePassword(store.id, newHash)
                    } else {
                        val admin = database.adminAccountDao().getByUsername(currentUsername)
                        if (admin != null) {
                            val newHash = PasswordValidator.hashPasswordAdmin(newPassword)
                            database.adminAccountDao().updatePassword(admin.id, newHash)
                        }
                    }
                    // Marcar código como usado
                    val reset = database.passwordResetDao().getValidByCode(code)
                    if (reset != null) {
                        database.passwordResetDao().markUsed(reset.id)
                    }

                    Toast.makeText(
                        this@PasswordRecoveryActivity,
                        R.string.recovery_success,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@PasswordRecoveryActivity,
                        R.string.recovery_error_code,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnAction.isEnabled = true
                    binding.btnAction.text = getString(R.string.recovery_button)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@PasswordRecoveryActivity, R.string.error_generic, Toast.LENGTH_LONG).show()
                binding.btnAction.isEnabled = true
                binding.btnAction.text = getString(R.string.recovery_button)
            }
        }
    }

    private fun openSupportWhatsApp() {
        val message = getString(R.string.recovery_support_message, currentUsername)
        val url = "https://wa.me/${Constants.SAVIA_SUPPORT_PHONE.replace("+", "")}?text=${Uri.encode(message)}"
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
