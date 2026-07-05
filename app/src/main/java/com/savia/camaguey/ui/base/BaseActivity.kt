package com.savia.camaguey.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savia.camaguey.R
import com.savia.camaguey.ui.cart.CartActivity
import com.savia.camaguey.ui.home.HomeActivity
import com.savia.camaguey.ui.map.MapActivity
import com.savia.camaguey.ui.settings.SettingsActivity

/**
 * BaseActivity: Actividad base para flujo de comprador con BottomNavigation.
 * Las actividades del comprador (Home, Map, Cart, Settings) extienden esta clase.
 * Usa Intents explícitos para navegación (compatible API 21, sin Fragment transactions pesadas).
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract val navItemId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.selectedItemId = navItemId

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == navItemId) {
                return@setOnItemSelectedListener true
            }

            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, HomeActivity::class.java)
                R.id.nav_map -> Intent(this, MapActivity::class.java)
                R.id.nav_cart -> Intent(this, CartActivity::class.java)
                R.id.nav_settings -> Intent(this, SettingsActivity::class.java)
                else -> null
            }

            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(it)
                overridePendingTransition(0, 0)
            }
            true
        }
    }
}
