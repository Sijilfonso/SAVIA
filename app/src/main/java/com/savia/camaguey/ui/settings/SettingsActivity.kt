package com.savia.camaguey.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.UserLocation
import com.savia.camaguey.databinding.ActivitySettingsBinding
import com.savia.camaguey.ui.base.BaseActivity
import com.savia.camaguey.util.Constants
import kotlinx.coroutines.launch

/**
 * SettingsActivity: Ajustes anónimos del comprador.
 * Ubicación GPS/manual, moneda preferida, borrar datos locales.
 * NO perfil de usuario, NO teléfono, NO contraseña, NO email.
 */
class SettingsActivity : BaseActivity() {

    override val navItemId: Int = R.id.nav_settings

    private lateinit var binding: ActivitySettingsBinding
    private val prefs by lazy { getSharedPreferences("savia_settings", Context.MODE_PRIVATE) }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNav)
        setupLocationSettings()
        setupCurrencySettings()
        setupClearData()
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        val modo = prefs.getString("location_mode", "default") ?: "default"
        binding.tvLocationMode.text = when (modo) {
            "gps" -> "GPS"
            "manual" -> "Manual"
            else -> "Por defecto (Camagüey)"
        }

        val moneda = prefs.getString("currency_pref", "AMBAS") ?: "AMBAS"
        binding.tvCurrencyPref.text = moneda
    }

    private fun setupLocationSettings() {
        binding.cardLocation.setOnClickListener {
            val options = arrayOf("Usar GPS", "Editar manualmente", "Por defecto (Camagüey)")
            AlertDialog.Builder(this)
                .setTitle("Ubicación")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> requestAndUseGPS()
                        1 -> showManualLocationDialog()
                        2 -> setDefaultLocation()
                    }
                }
                .show()
        }
    }

    /**
     * FASE 3: Solicita permiso GPS y obtiene ubicación actual.
     * Guarda en UserLocation (Room) para uso en Mapa y Ruta.
     */
    private fun requestAndUseGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        getGPSLocation()
    }

    private fun getGPSLocation() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                AlertDialog.Builder(this)
                    .setTitle("GPS desactivado")
                    .setMessage("Por favor activa el GPS en los ajustes del dispositivo.")
                    .setPositiveButton("OK", null)
                    .show()
                return
            }

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    saveLocationToRoom(location.latitude, location.longitude, modo = "gps")
                    prefs.edit().putString("location_mode", "gps").apply()
                    binding.tvLocationMode.text = "GPS"
                    try {
                        locationManager.removeUpdates(this)
                    } catch (_: Exception) {}
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (isNetworkEnabled) {
                locationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER,
                    locationListener,
                    Looper.getMainLooper()
                )
            }
            if (isGpsEnabled) {
                locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    locationListener,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            AlertDialog.Builder(this)
                .setTitle("Permiso denegado")
                .setMessage("No se pudo acceder a la ubicación. Verifica los permisos.")
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * FASE 3: Diálogo para editar ubicación manualmente (lat/lng).
     * Guarda en UserLocation (Room).
     */
    private fun showManualLocationDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val editLat = EditText(this).apply {
            hint = "Latitud (ej: 21.3833)"
            setText(Constants.DEFAULT_LAT.toString())
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val editLng = EditText(this).apply {
            hint = "Longitud (ej: -77.9167)"
            setText(Constants.DEFAULT_LNG.toString())
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        layout.addView(editLat)
        layout.addView(editLng)

        AlertDialog.Builder(this)
            .setTitle("Editar ubicación manualmente")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                try {
                    val lat = editLat.text.toString().toDouble()
                    val lng = editLng.text.toString().toDouble()
                    if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                        saveLocationToRoom(lat, lng, modo = "manual")
                        prefs.edit().putString("location_mode", "manual").apply()
                        binding.tvLocationMode.text = "Manual"
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Coordenadas inválidas")
                            .setMessage("Latitud debe estar entre -90 y 90. Longitud entre -180 y 180.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } catch (e: NumberFormatException) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Por favor introduce números válidos.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setDefaultLocation() {
        saveLocationToRoom(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG, modo = "default", zona = "Centro Histórico", direccion = "Camagüey, Cuba")
        prefs.edit().putString("location_mode", "default").apply()
        binding.tvLocationMode.text = "Por defecto (Camagüey)"
    }

    private fun saveLocationToRoom(lat: Double, lng: Double, modo: String = "manual", zona: String? = null, direccion: String? = null) {
        lifecycleScope.launch {
            try {
                val database = SaviaDatabase.getInstance(this@SettingsActivity)
                database.userLocationDao().deleteAll()
                database.userLocationDao().insert(
                    UserLocation(
                        latitud = lat,
                        longitud = lng,
                        zona = zona,
                        direccionTexto = direccion,
                        modoObtencion = modo
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSLocation()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permiso necesario")
                    .setMessage("El permiso de ubicación es necesario para usar el GPS. Puedes usar la ubicación manual como alternativa.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun setupCurrencySettings() {
        binding.cardCurrency.setOnClickListener {
            val options = arrayOf("CUP", "USD", "MLC", "Ambas")
            AlertDialog.Builder(this)
                .setTitle("Moneda preferida")
                .setItems(options) { _, which ->
                    val selected = options[which]
                    prefs.edit().putString("currency_pref", selected.uppercase()).apply()
                    binding.tvCurrencyPref.text = selected.uppercase()
                }
                .show()
        }
    }

    private fun setupClearData() {
        binding.cardClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.settings_clear_data)
                .setMessage(R.string.settings_clear_confirm)
                .setPositiveButton(R.string.settings_clear_yes) { _, _ ->
                    lifecycleScope.launch {
                        val database = SaviaDatabase.getInstance(this@SettingsActivity)
                        database.cartDao().deleteAll()
                        database.userLocationDao().deleteAll()
                        prefs.edit().clear().apply()
                        loadCurrentSettings()
                    }
                }
                .setNegativeButton(R.string.settings_clear_cancel, null)
                .show()
        }
    }
}
