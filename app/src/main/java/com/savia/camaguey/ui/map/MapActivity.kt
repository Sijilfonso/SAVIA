package com.savia.camaguey.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.lifecycleScope
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityMapBinding
import com.savia.camaguey.ui.base.BaseActivity
import com.savia.camaguey.ui.store.StoreProfileActivity
import com.savia.camaguey.util.Constants
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * MapActivity: Mapa general con todos los negocios aprobados.
 * OSMDroid con marcadores, tiles OpenStreetMap, ubicación del usuario.
 * Sin Google Play Services. Compatible Huawei.
 */
class MapActivity : BaseActivity() {

    override val navItemId: Int = R.id.nav_map

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private lateinit var storeRepository: StoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid config (must be before setContentView)
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNav)

        val database = SaviaDatabase.getInstance(this)
        storeRepository = StoreRepository(database)

        setupMap()
        loadStores()
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)

        // Centro en Camagüey por defecto
        val startPoint = GeoPoint(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)

        // Overlay de ubicación (opcional, no requiere permisos al inicio)
        try {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
            locationOverlay.enableMyLocation()
            mapView.overlays.add(locationOverlay)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadStores() {
        lifecycleScope.launch {
            try {
                val stores = storeRepository.getAllApprovedList()
                addMarkers(stores)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addMarkers(stores: List<com.savia.camaguey.data.model.Store>) {
        stores.forEach { store ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(store.latitud, store.longitud)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = store.nombrePublico
                snippet = "${store.categoriaPrincipal} · ${store.zona}"
                setOnMarkerClickListener { _, _ ->
                    openStoreProfile(store.id)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    private fun openStoreProfile(storeId: String) {
        val intent = Intent(this, StoreProfileActivity::class.java).apply {
            putExtra(StoreProfileActivity.EXTRA_STORE_ID, storeId)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
