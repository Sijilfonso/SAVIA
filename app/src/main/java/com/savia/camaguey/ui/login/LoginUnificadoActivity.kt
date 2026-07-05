package com.savia.camaguey.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.repository.AuthRepository
import com.savia.camaguey.databinding.ActivityLoginBinding
import com.savia.camaguey.ui.admin.AdminActivity
import com.savia.camaguey.ui.panel.PanelVendedorActivity
import com.savia.camaguey.ui.register.RegisterStoreActivity
import kotlinx.coroutines.launch

/**
 * LoginUnificadoActivity: Login único para vendedores y admins.
 * Username + password. El backend (Room local) responde con rol.
 * Redirige según rol: vendedor → PanelVendedor, admin → Admin.
 * NO hay login separado para admin.
 */
class LoginUnificadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SaviaDatabase.getInstance(this)
        authRepository = AuthRepository(database)

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, PasswordRecoveryActivity::class.java))
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterStoreActivity::class.java))
        }
    }

    private fun doLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty()) {
            binding.etUsername.error = "Ingresa tu usuario"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Ingresa tu contraseña"
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                val result = authRepository.login(username, password)
                when (result) {
                    is AuthRepository.AuthResult.Success -> {
                        // Guardar sesión en SharedPreferences
                        val prefs = getSharedPreferences("savia_session", MODE_PRIVATE)
                        prefs.edit()
                            .putString("session_username", username)
                            .putString("session_rol", result.rol)
                            .putString("session_user_id", result.userId)
                            .putString("session_nombre", result.nombre)
                            .apply()

                        // Redirigir según rol
                        val intent = when (result.rol) {
                            "admin" -> Intent(this@LoginUnificadoActivity, AdminActivity::class.java)
                            else -> Intent(this@LoginUnificadoActivity, PanelVendedorActivity::class.java)
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is AuthRepository.AuthResult.Error -> {
                        Toast.makeText(this@LoginUnificadoActivity, result.message, Toast.LENGTH_LONG).show()
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.login_button)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginUnificadoActivity, R.string.error_generic, Toast.LENGTH_LONG).show()
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.login_button)
            }
        }
    }
}
