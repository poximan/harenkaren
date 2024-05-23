package com.example.demo.fragment.maps

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.R
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.UnidSocial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private var clickedGeoPoint: GeoPoint? = null

    // Método llamado cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        getPosiciones()
    }

    private fun getPosiciones() {

        val viewModelScope = viewLifecycleOwner.lifecycleScope
        val unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewModelScope)
            .unSocDao()

        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            val unidSociales =
                unSocDAO.getAll().sortedWith(compareBy({ it.recorrId }, { it.orden }))

            // ---------> HILO PRINCIPAL
            withContext(Dispatchers.Main) {
                var routePoints = emptyList<GeoPoint>()
                var recorr = unidSociales.first().recorrId

                for (unSoc in unidSociales) {
                    if (recorr != unSoc.recorrId) {
                        agregarPolilinea(routePoints)
                        routePoints = emptyList()
                        recorr = unSoc.recorrId
                    }
                    // acumular el recorrido en transito. sera insertado al mapa cuando aparezca uno nuevo
                    routePoints = routePoints.plus(geo(unSoc))

                    // ---------> HILO BACKGOUND
                    withContext(Dispatchers.IO) {

                        val total = unSocDAO.getMaxRegistro(unSoc.recorrId)
                        // ---------> HILO PRINCIPAL
                        withContext(Dispatchers.Main) {
                            agregarMarcador(
                                unSoc,
                                total
                            )  // los puntos se insertan en cada pasada "mapView.overlays.add"
                        }
                    }
                }
                agregarPolilinea(routePoints)
                val startPoint = routePoints.first()
                mapController.setCenter(startPoint)
                mapView.invalidate() // Actualizar el mapa
            }
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

    private fun agregarPolilinea(routePoints: List<GeoPoint>) {
        val polyline = Polyline().apply {
            setPoints(routePoints)
            color = Color.RED
            width = 5.0f
        }
        mapView.overlays.add(polyline)
    }

    private fun agregarMarcador(unSoc: UnidSocial, total: Int) {

        val punto = GeoPoint(unSoc.latitud, unSoc.longitud)
        val marker = Marker(mapView)
        marker.position = punto
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)


        val infoWindow = CustomMarkerInfoWindow(
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
    }

    private fun geo(unSoc: UnidSocial): GeoPoint {
        return GeoPoint(unSoc.latitud, unSoc.longitud)
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