package com.example.demo.fragment.maps

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.demo.R
import com.example.demo.activity.HomeActivity
import com.example.demo.model.UnidSocial
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class OSMFragment : SuperMapa(), MapEventsReceiver {

    private val colors = listOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN
    )

    private lateinit var mapView: MapView
    private lateinit var webView: WebView
    private lateinit var mapController: IMapController

    private var polylines = mutableListOf<Polyline>()
    private var markers = mutableListOf<Marker>()

    // Método llamado cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        webView = view.findViewById(R.id.webViewHeat)
        mapView = view.findViewById(R.id.mapView)
        webView.visibility = View.INVISIBLE
        mapView.visibility = View.VISIBLE

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Inicializar el controlador del mapa
        mapController = mapView.controller
        mapController.setZoom(15.0)     // numero mayor zoom mas cercano 30.0 muchisimo

        // Agregar receptor de eventos de clic en el mapa
        val mapEventsOverlay = MapEventsOverlay(this)
        mapView.overlays.add(mapEventsOverlay)

        // Establecer el agente de usuario para OSMDroid
        Configuration.getInstance().userAgentValue = "AGENTE_OSM_HARENKAREN"

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.clearAnimation()
    }

    private fun mensajeError() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.osm_noregTit))
        builder.setMessage(R.string.osm_noregMsj)

        builder.setPositiveButton(R.string.osm_noregMsjBtn) { _, _ -> }
        builder.setNegativeButton(getString(R.string.varias_volver)) { _, _ ->
            findNavController().navigateUp()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun findNavController(): NavController {
        return (activity as HomeActivity).findNavController(R.id.navHostHome)
    }

    override fun resolverVisibilidad() {
        val geoPoint: GeoPoint = puntoMedioPosiciones(unSocList)

        mapController.setCenter(geoPoint)
        mapController.setZoom(geoPoint.altitude)
        mostrarMapaRecorridos(unSocList)
    }

    private fun mostrarMapaRecorridos(unSocList: List<UnidSocial>) {

        var routePoints = emptyList<GeoPoint>()
        var nvaPoli = 0

        removerPolilinea()
        removerMarcadores()

        try {
            var recorr = unSocList.first().recorrId

            for (unSoc in unSocList) {
                if (recorr != unSoc.recorrId) {
                    agregarPolilinea(routePoints, nvaPoli)
                    nvaPoli++
                    routePoints = emptyList()
                    recorr = unSoc.recorrId
                }
                // acumular el recorrido en transito. sera insertado al mapa cuando aparezca uno nuevo
                routePoints = routePoints.plus(geo(unSoc))
                agregarMarcador(unSoc)  // los puntos se insertan en cada pasada "mapView.overlays.add"
            }
            agregarPolilinea(routePoints, nvaPoli)

            val currentCenter = mapView.mapCenter as GeoPoint
            // Verificar si currentCenter es (0.0, 0.0, 0.0)
            var startPoint: GeoPoint =
                if (currentCenter.latitude == 0.0 && currentCenter.longitude == 0.0) {
                    routePoints.first()
                } else {
                    currentCenter
                }

            val currentZoomLevel = mapView.zoomLevelDouble
            mapView.invalidate() // Actualizar la vista del mapa
            mapView.controller.setZoom(currentZoomLevel)
            mapView.controller.setCenter(startPoint) // Restaurar el centro del mapa
        } catch (e: NoSuchElementException) {
            val context = requireContext()
            Toast.makeText(
                context,
                context.getString(R.string.osm_mostrar),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun agregarPolilinea(routePoints: List<GeoPoint>, nvaPoli: Int) {
        val polyline = Polyline().apply {
            setPoints(routePoints)
            color = colors[nvaPoli % colors.size]
            width = 5.0f
        }
        mapView.overlays.add(polyline)
        polylines.add(polyline)
    }

    private fun removerPolilinea() {
        if (polylines.isNotEmpty()) {
            polylines.forEach { mapView.overlays.remove(it) }
            polylines.clear()
        }
    }

    private fun removerMarcadores() {
        if (markers.isNotEmpty()) {
            markers.forEach { mapView.overlays.remove(it) }
            markers.clear()
        }
    }

    private fun agregarMarcador(unSoc: UnidSocial) {

        val punto = geo(unSoc)
        val marker = Marker(mapView)
        marker.position = punto
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)

        val infoWindow = BarCharInfoWindow(
            requireContext(), unSoc,
            R.layout.fragment_osm_bubble, mapView
        )

        marker.setOnMarkerClickListener { _, _ ->
            if (infoWindow.isOpen) {
                infoWindow.close()
            } else {
                marker.showInfoWindow()
            }
            true
        }
        marker.infoWindow = infoWindow
        mapView.overlays.add(marker)
        markers.add(marker)
    }

    private fun geo(unSoc: UnidSocial): GeoPoint {
        return GeoPoint(unSoc.latitud, unSoc.longitud)
    }

    // un toque en el mapa
    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

        p?.let { geoPoint ->
            val latitude = String.format("%.6f", geoPoint.latitude)
            val longitude = String.format("%.6f", geoPoint.longitude)
            val toastText = "Lat.: $latitude, Long.: $longitude"
            Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
        }
        return true     // el evento ha sido manejado
    }

    // se mantiene presionado en el mapa
    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}