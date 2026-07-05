package com.savia.camaguey.ui.route

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.savia.camaguey.R
import com.savia.camaguey.data.local.SaviaDatabase
import com.savia.camaguey.data.repository.RouteRepository
import com.savia.camaguey.data.repository.StoreRepository
import com.savia.camaguey.databinding.ActivityRouteBinding
import com.savia.camaguey.util.Constants
import com.savia.camaguey.util.Haversine
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * RouteActivity: Mapa OSMDroid con ruta óptima TSP + polyline punteada.
 * Panel inferior con paradas ordenadas, checkbox completado, modo transporte.
 * Fallback Haversine si no hay red. Botón "Abrir en Google Maps" con waypoints.
 */
class RouteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORE_IDS = "store_ids"
    }

    private lateinit var binding: ActivityRouteBinding
    private lateinit var mapView: MapView
    private lateinit var database: SaviaDatabase
    private lateinit var routeRepository: RouteRepository
    private lateinit var storeRepository: StoreRepository
    private var storeIds: List<String> = emptyList()
    private var currentModo: RouteRepository.ModoTransporte = RouteRepository.ModoTransporte.CAMINANDO
    private var routeResult: RouteRepository.RouteResult? = null
    private var userLat: Double = Constants.DEFAULT_LAT
    private var userLng: Double = Constants.DEFAULT_LNG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storeIds = intent.getStringArrayListExtra(EXTRA_STORE_IDS) ?: emptyList()
        if (storeIds.isEmpty()) {
            finish()
            return
        }

        database = SaviaDatabase.getInstance(this)
        routeRepository = RouteRepository(database)
        storeRepository = StoreRepository(database)

        setupMap()
        setupModoTransporte()
        setupOpenGoogleMaps()
        loadRoute()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(15.0)
    }

    private fun setupModoTransporte() {
        binding.btnModoCaminando.setOnClickListener { cambiarModo(RouteRepository.ModoTransporte.CAMINANDO) }
        binding.btnModoMoto.setOnClickListener { cambiarModo(RouteRepository.ModoTransporte.MOTO) }
        binding.btnModoAuto.setOnClickListener { cambiarModo(RouteRepository.ModoTransporte.AUTO) }
    }

    private fun cambiarModo(modo: RouteRepository.ModoTransporte) {
        currentModo = modo
        updateModoButtonsUI()
        loadRoute()
    }

    private fun updateModoButtonsUI() {
        val selectedColor = resources.getColor(R.color.savia_green_dark)
        val selectedTextColor = resources.getColor(R.color.savia_white)
        val unselectedColor = resources.getColor(R.color.savia_bg_card)
        val unselectedTextColor = resources.getColor(R.color.savia_text_primary)

        binding.btnModoCaminando.apply {
            setBackgroundColor(if (currentModo == RouteRepository.ModoTransporte.CAMINANDO) selectedColor else unselectedColor)
            setTextColor(if (currentModo == RouteRepository.ModoTransporte.CAMINANDO) selectedTextColor else unselectedTextColor)
        }
        binding.btnModoMoto.apply {
            setBackgroundColor(if (currentModo == RouteRepository.ModoTransporte.MOTO) selectedColor else unselectedColor)
            setTextColor(if (currentModo == RouteRepository.ModoTransporte.MOTO) selectedTextColor else unselectedTextColor)
        }
        binding.btnModoAuto.apply {
            setBackgroundColor(if (currentModo == RouteRepository.ModoTransporte.AUTO) selectedColor else unselectedColor)
            setTextColor(if (currentModo == RouteRepository.ModoTransporte.AUTO) selectedTextColor else unselectedTextColor)
        }
    }

    private fun setupOpenGoogleMaps() {
        binding.btnOpenGoogleMaps.setOnClickListener {
            val result = routeResult ?: return@setOnClickListener
            openGoogleMapsWithWaypoints(result)
        }
    }

    private fun loadRoute() {
        lifecycleScope.launch {
            try {
                val userLocation = database.userLocationDao().get()
                userLat = userLocation?.latitud ?: Constants.DEFAULT_LAT
                userLng = userLocation?.longitud ?: Constants.DEFAULT_LNG

                // TSP Nearest Neighbor
                val result = routeRepository.calculateOptimalRoute(
                    userLat, userLng, storeIds, currentModo
                )

                if (result.stops.isEmpty()) {
                    finish()
                    return@launch
                }

                routeResult = result

                // Dibujar ruta en mapa
                drawRoute(userLat, userLng, result)

                // Panel inferior con paradas
                setupStopsPanel(result)

                // Info resumen
                binding.tvTotalDistance.text = String.format("%.1f km", result.distanciaTotalKm)
                binding.tvTotalTime.text = Haversine.formatTime(result.tiempoTotalMin)

                updateModoButtonsUI()
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        }
    }

    private fun drawRoute(userLat: Double, userLng: Double, result: RouteRepository.RouteResult) {
        mapView.overlays.clear()

        val points = mutableListOf<GeoPoint>()
        points.add(GeoPoint(userLat, userLng))

        result.stops.forEach { stop ->
            points.add(GeoPoint(stop.store.latitud, stop.store.longitud))
        }

        // Polyline punteada (fallback Haversine)
        val polyline = Polyline(mapView).apply {
            setPoints(points)
            outlinePaint.color = Color.parseColor("#C89F3C")
            outlinePaint.strokeWidth = 8f
            outlinePaint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
        }
        mapView.overlays.add(polyline)

        // Marcador usuario
        val userMarker = Marker(mapView).apply {
            position = GeoPoint(userLat, userLng)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Tú"
        }
        mapView.overlays.add(userMarker)

        // Marcadores paradas
        result.stops.forEachIndexed { index, stop ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(stop.store.latitud, stop.store.longitud)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "${index + 1}. ${stop.store.nombrePublico}"
                snippet = "${String.format("%.1f", stop.distanciaDesdeAnteriorKm)} km · ${stop.tiempoEstimadoMin} min"
            }
            mapView.overlays.add(marker)
        }

        // Centrar mapa en ruta
        if (points.isNotEmpty()) {
            mapView.controller.setCenter(points[0])
            mapView.zoomToBoundingBox(
                org.osmdroid.util.BoundingBox.fromGeoPoints(points),
                false, 50, 15.0, 0L
            )
        }
        mapView.invalidate()
    }

    private fun setupStopsPanel(result: RouteRepository.RouteResult) {
        binding.stopsContainer.removeAllViews()
        result.stops.forEachIndexed { index, stop ->
            val stopView = layoutInflater.inflate(R.layout.item_route_stop, binding.stopsContainer, false)
            val tvNumber = stopView.findViewById<android.widget.TextView>(R.id.tvStopNumber)
            val tvName = stopView.findViewById<android.widget.TextView>(R.id.tvStopName)
            val tvInfo = stopView.findViewById<android.widget.TextView>(R.id.tvStopInfo)
            val cbComplete = stopView.findViewById<android.widget.CheckBox>(R.id.cbComplete)

            tvNumber.text = "${index + 1}"
            tvName.text = stop.store.nombrePublico
            tvInfo.text = "${String.format("%.1f", stop.distanciaDesdeAnteriorKm)} km · ${stop.tiempoEstimadoMin} min"
            cbComplete.setOnCheckedChangeListener { _, isChecked ->
                tvName.paint.isStrikeThruText = isChecked
                tvName.alpha = if (isChecked) 0.5f else 1.0f
            }
            binding.stopsContainer.addView(stopView)
        }
    }

    /**
     * Abre Google Maps con waypoints de la ruta óptima.
     * Formato: https://www.google.com/maps/dir/?api=1&origin=...&destination=...&waypoints=...
     */
    private fun openGoogleMapsWithWaypoints(result: RouteRepository.RouteResult) {
        try {
            val origin = "${userLat},${userLng}"
            val destination = if (result.stops.isNotEmpty()) {
                "${result.stops.last().store.latitud},${result.stops.last().store.longitud}"
            } else {
                origin
            }
            val waypoints = if (result.stops.size > 1) {
                result.stops.dropLast(1).joinToString("|") {
                    "${it.store.latitud},${it.store.longitud}"
                }
            } else {
                ""
            }

            val uriBuilder = Uri.parse("https://www.google.com/maps/dir/").buildUpon()
                .appendQueryParameter("api", "1")
                .appendQueryParameter("origin", origin)
                .appendQueryParameter("destination", destination)
            if (waypoints.isNotEmpty()) {
                uriBuilder.appendQueryParameter("waypoints", waypoints)
            }

            val intent = Intent(Intent.ACTION_VIEW, uriBuilder.build())
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
