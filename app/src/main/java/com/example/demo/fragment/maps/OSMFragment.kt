package com.example.demo.fragment.maps

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class OSMFragment : Fragment(), MapEventsReceiver {

    private val args: OSMFragmentArgs by navArgs()
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private var clickedGeoPoint: GeoPoint? = null

    // Método llamado cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño de la vista del fragmento
        val view = inflater.inflate(R.layout.fragment_osm, container, false)

        // Inicializar el mapa
        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Inicializar el controlador del mapa
        mapController = mapView.controller
        mapController.setZoom(15.0)     // numero mayor zoom mas cercano 30.0 muchisimo

        // Agregar receptor de eventos de clic en el mapa
        val mapEventsOverlay = MapEventsOverlay(this)
        mapView.overlays.add(0, mapEventsOverlay)

        // Establecer el agente de usuario para OSMDroid
        Configuration.getInstance().userAgentValue = "AGENTE_OSM_HARENKAREN"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routePoints = listOf(
            GeoPoint(args.coordenadas.lat, args.coordenadas.lon)
        )

        val startPoint = routePoints.first()
        mapController.setCenter(startPoint)

        // Agregar marcador en el punto
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = requireContext().resources.getDrawable(R.mipmap.ic_cachorris)
        marker.title = "Punto"
        mapView.overlays.add(marker)

        val polyline = Polyline().apply {
            setPoints(routePoints)
            color = Color.RED
            width = 5.0f
        }

        mapView.overlays.add(polyline)
        mapView.invalidate() // Actualizar el mapa
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    // un toque en el mapa
    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        // Guardar las coordenadas en clickedGeoPoint
        clickedGeoPoint = p

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