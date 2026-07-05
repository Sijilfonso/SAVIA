package com.savia.camaguey.ui.register

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.databinding.ActivityRegisterStoreBinding
import com.savia.camaguey.util.Constants
import com.savia.camaguey.util.PasswordValidator
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.UUID

/**
 * RegisterStoreActivity: Wizard de 4 pasos para registrar un negocio.
 * Paso 1: Representante (nombre, CI, teléfono)
 * Paso 2: Negocio (nombre, tipo MIPYME/TCP/PDL, licencia, dirección, categoría, horario, entrega, web, teléfono recuperación)
 * Paso 3: Ubicación POI + Credenciales (username, password)
 * Paso 4: Confirmación de verificación pendiente
 * Al finalizar, guarda en Room como estadoVerificacion = "pendiente".
 */
class RegisterStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStoreBinding
    private var currentStep = 1

    private var selectedLat = Constants.DEFAULT_LAT
    private var selectedLng = Constants.DEFAULT_LNG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityRegisterStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        updateStepUI()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnNext.setOnClickListener { onNext() }
        binding.btnPrev.setOnClickListener { onPrev() }
    }

    private fun setupMap() {
        binding.mapRegister.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapRegister.setMultiTouchControls(true)
        binding.mapRegister.setBuiltInZoomControls(true)
        binding.mapRegister.controller.setZoom(15.0)
        binding.mapRegister.controller.setCenter(GeoPoint(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG))

        val marker = Marker(binding.mapRegister).apply {
            position = GeoPoint(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Ubica tu negocio"
            isDraggable = true
            setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                override fun onMarkerDrag(marker: Marker?) {
                    marker?.let {
                        selectedLat = it.position.latitude
                        selectedLng = it.position.longitude
                    }
                }
                override fun onMarkerDragEnd(marker: Marker?) {
                    marker?.let {
                        selectedLat = it.position.latitude
                        selectedLng = it.position.longitude
                    }
                }
                override fun onMarkerDragStart(marker: Marker?) {}
            })
        }
        binding.mapRegister.overlays.add(marker)
    }

    private fun updateStepUI() {
        // Hide all steps
        binding.step1Content.visibility = android.view.View.GONE
        binding.step2Content.visibility = android.view.View.GONE
        binding.step3Content.visibility = android.view.View.GONE
        binding.step4Content.visibility = android.view.View.GONE

        // Show current step
        when (currentStep) {
            1 -> {
                binding.step1Content.visibility = android.view.View.VISIBLE
                binding.tvStepLabel.text = getString(R.string.register_step_1)
                binding.btnPrev.visibility = android.view.View.GONE
                binding.btnNext.text = "Siguiente"
                updateIndicators(1)
            }
            2 -> {
                binding.step2Content.visibility = android.view.View.VISIBLE
                binding.tvStepLabel.text = getString(R.string.register_step_2)
                binding.btnPrev.visibility = android.view.View.VISIBLE
                binding.btnNext.text = "Siguiente"
                updateIndicators(2)
            }
            3 -> {
                binding.step3Content.visibility = android.view.View.VISIBLE
                binding.tvStepLabel.text = getString(R.string.register_step_3)
                binding.btnPrev.visibility = android.view.View.VISIBLE
                binding.btnNext.text = getString(R.string.register_submit)
                updateIndicators(3)
            }
            4 -> {
                binding.step4Content.visibility = android.view.View.VISIBLE
                binding.tvStepLabel.text = getString(R.string.register_step_4)
                binding.btnPrev.visibility = android.view.View.GONE
                binding.btnNext.visibility = android.view.View.GONE
                updateIndicators(4)
            }
        }
    }

    private fun updateIndicators(activeStep: Int) {
        val activeColor = resources.getColor(R.color.savia_gold)
        val inactiveColor = resources.getColor(R.color.savia_white)
        val activeTextColor = resources.getColor(R.color.savia_white)
        val inactiveTextColor = resources.getColor(R.color.savia_text_primary)

        val steps = listOf(binding.step1Indicator, binding.step2Indicator, binding.step3Indicator, binding.step4Indicator)
        steps.forEachIndexed { index, view ->
            if (index + 1 <= activeStep) {
                view.setBackgroundColor(activeColor)
                view.setTextColor(activeTextColor)
            } else {
                view.setBackgroundColor(inactiveColor)
                view.setTextColor(inactiveTextColor)
            }
        }
    }

    private fun onNext() {
        if (!validateCurrentStep()) return

        if (currentStep < 3) {
            currentStep++
            updateStepUI()
        } else if (currentStep == 3) {
            submitRegistration()
        }
    }

    private fun onPrev() {
        if (currentStep > 1) {
            currentStep--
            updateStepUI()
        }
    }

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> {
                if (binding.etRepNombre.text.isNullOrBlank()) {
                    binding.etRepNombre.error = "Requerido"
                    false
                } else if (binding.etRepCI.text.isNullOrBlank()) {
                    binding.etRepCI.error = "Requerido"
                    false
                } else if (binding.etRepTelefono.text.isNullOrBlank()) {
                    binding.etRepTelefono.error = "Requerido"
                    false
                } else true
            }
            2 -> {
                if (binding.etStoreName.text.isNullOrBlank()) {
                    binding.etStoreName.error = "Requerido"
                    false
                } else if (binding.rgTipoEntidad.checkedRadioButtonId == -1) {
                    Toast.makeText(this, "Selecciona el tipo de entidad", Toast.LENGTH_SHORT).show()
                    false
                } else if (binding.etLicencia.text.isNullOrBlank()) {
                    binding.etLicencia.error = "Requerido"
                    false
                } else if (binding.etDireccion.text.isNullOrBlank()) {
                    binding.etDireccion.error = "Requerido"
                    false
                } else if (binding.etCategoria.text.isNullOrBlank()) {
                    binding.etCategoria.error = "Requerido"
                    false
                } else if (binding.etTelefonoRecuperacion.text.isNullOrBlank()) {
                    binding.etTelefonoRecuperacion.error = "Requerido"
                    false
                } else true
            }
            3 -> {
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString()
                val confirm = binding.etPasswordConfirm.text.toString()

                if (username.isEmpty()) {
                    binding.etUsername.error = "Requerido"
                    false
                } else if (password.length < Constants.VENDOR_PASSWORD_MIN_LENGTH) {
                    binding.etPassword.error = "Mínimo ${Constants.VENDOR_PASSWORD_MIN_LENGTH} caracteres"
                    false
                } else if (!PasswordValidator.validateVendor(password).first) {
                    binding.etPassword.error = "Debe incluir mayúscula, minúscula y número"
                    false
                } else if (password != confirm) {
                    binding.etPasswordConfirm.error = "Las contraseñas no coinciden"
                    false
                } else true
            }
            else -> true
        }
    }

    private fun submitRegistration() {
        binding.btnNext.isEnabled = false
        binding.btnNext.text = getString(R.string.loading)

        lifecycleScope.launch {
            try {
                val tipoEntidad = when (binding.rgTipoEntidad.checkedRadioButtonId) {
                    R.id.rbMIPYME -> "MIPYME"
                    R.id.rbTCP -> "TCP"
                    R.id.rbPDL -> "PDL"
                    else -> "TCP"
                }

                val idInterno = "CMP-${(10000..99999).random()}"
                val bcryptHasher = BCrypt.withDefaults()

                val store = Store(
                    id = UUID.randomUUID().toString(),
                    idInterno = idInterno,
                    nombrePublico = binding.etStoreName.text.toString().trim(),
                    tipoEntidad = tipoEntidad,
                    representanteNombre = binding.etRepNombre.text.toString().trim(),
                    representanteCI = binding.etRepCI.text.toString().trim(),
                    representanteTelefono = binding.etRepTelefono.text.toString().trim(),
                    licenciaEstatal = binding.etLicencia.text.toString().trim(),
                    direccionCompleta = binding.etDireccion.text.toString().trim(),
                    zona = "Camagüey", // Simplificado para MVP
                    latitud = selectedLat,
                    longitud = selectedLng,
                    categoriaPrincipal = binding.etCategoria.text.toString().trim(),
                    telefonoWhatsApp = binding.etRepTelefono.text.toString().trim(),
                    telefonoRecuperacion = binding.etTelefonoRecuperacion.text.toString().trim(),
                    webUrl = binding.etWebUrl.text.toString().trim().takeIf { it.isNotEmpty() },
                    fotoLocalUrl = null,
                    horario = binding.etHorario.text.toString().trim(),
                    entregaInfo = binding.etEntrega.text.toString().trim(),
                    planDestacado = false,
                    planTrialHasta = null,
                    suscripcionActiva = false,
                    suscripcionVence = null,
                    verificado = false,
                    estadoVerificacion = "pendiente",
                    username = binding.etUsername.text.toString().trim(),
                    passwordHash = bcryptHasher.hashToString(10, binding.etPassword.text.toString().toCharArray()),
                    rol = "vendedor"
                )

                val database = SaviaDatabase.getInstance(this@RegisterStoreActivity)
                database.storeDao().insert(store)

                currentStep = 4
                updateStepUI()
                Toast.makeText(this@RegisterStoreActivity, "Solicitud enviada exitosamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RegisterStoreActivity, R.string.error_generic, Toast.LENGTH_LONG).show()
                binding.btnNext.isEnabled = true
                binding.btnNext.text = getString(R.string.register_submit)
            }
        }
    }
}
