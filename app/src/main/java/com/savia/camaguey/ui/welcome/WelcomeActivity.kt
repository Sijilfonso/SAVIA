package com.savia.camaguey.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.savia.camaguey.databinding.ActivityWelcomeBinding
import com.savia.camaguey.ui.home.HomeActivity
import com.savia.camaguey.ui.login.LoginUnificadoActivity

/**
 * WelcomeActivity: Pantalla de entrada SIN fricción.
 * Logo + eslogan "Camagüey crece aquí" + botón "Comenzar a comprar" + link negocio.
 * NO pide teléfono, NO pide GPS, NO permisos obligatorios.
 */
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón principal: entrar a comprar sin login
        binding.btnStartShopping.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Link inferior: acceso para negocios (login unificado)
        binding.tvBusinessLink.setOnClickListener {
            startActivity(Intent(this, LoginUnificadoActivity::class.java))
        }
    }
}
