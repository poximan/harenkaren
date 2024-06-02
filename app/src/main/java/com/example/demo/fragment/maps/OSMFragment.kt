package com.example.demo.fragment.maps

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.R
import com.example.demo.dao.UnSocDAO
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

    private lateinit var unSocDAO: UnSocDAO

    private lateinit var mapController: IMapController
    private lateinit var mapView: MapView
    private var polylines = mutableListOf<Polyline>()
    private var markers = mutableListOf<Marker>()

    private lateinit var webView: WebView

    private lateinit var chkMapaCalor: CheckBox
    private lateinit var filtroAnio: Spinner
    private lateinit var anios: List<Int>

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
        mapView.overlays.add(mapEventsOverlay)

        // Establecer el agente de usuario para OSMDroid
        Configuration.getInstance().userAgentValue = "AGENTE_OSM_HARENKAREN"
        filtroAnio = view.findViewById(R.id.filtroAnio)

        webView = view.findViewById(R.id.webViewHeat)

        val diaDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .diaDao()

        CoroutineScope(Dispatchers.IO).launch {
            anios = diaDAO.getTodosAnios()
            withContext(Dispatchers.Main) {
                // Configurar el Spinner
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, anios)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                filtroAnio.adapter = adapter
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chkMapaCalor = view.findViewById(R.id.chk_mapacalor)
        chkMapaCalor.setOnCheckedChangeListener { _, isChecked ->
            val item = filtroAnio.selectedItemPosition
            filtroAnio.setSelection(item)
        }

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        configurarFiltroAnio(view)
    }

    private fun configurarFiltroAnio(view: View) {

        filtroAnio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val anioSeleccionado = anios[position]
                getInvolucrados(anioSeleccionado) {
                    if(chkMapaCalor.isChecked)
                        verMapaCalor(true, it)
                    else
                        verMapaRecorridos(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Forzar la llamada manualmente al método onItemSelected
        val defaultPosition = anios.size - 1
        filtroAnio.setSelection(defaultPosition)

        filtroAnio.onItemSelectedListener?.onItemSelected(
            filtroAnio,
            view,
            defaultPosition,
            filtroAnio.getItemIdAtPosition(defaultPosition)
        )
    }

    private fun getInvolucrados(anio: Int, callback: (List<UnidSocial>) -> Unit) {
        var unSocList: List<UnidSocial>
        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            unSocList =
                unSocDAO.getAllPorAnio(anio.toString()).sortedWith(compareBy({ it.recorrId }, { it.orden }))

            // ---------> HILO PRINCIPAL
            withContext(Dispatchers.Main) {
                callback(unSocList)
            }
        }
    }

    private fun verMapaCalor(isChecked: Boolean, unSocList: List<UnidSocial>) {
        if (isChecked) {
            webView.visibility = View.VISIBLE
            mapView.visibility = View.GONE

            val mapaCalor = MapaCalor(webView, mapView.mapCenter as GeoPoint)
            mapaCalor.mostrarMapaCalor(unSocList)

        } else {
            webView.visibility = View.GONE
            mapView.visibility = View.VISIBLE
        }
    }

    private fun verMapaRecorridos(unSocList: List<UnidSocial>) {

        var routePoints = emptyList<GeoPoint>()
        var recorr = unSocList.first().recorrId

        removerPolilinea()
        removerMarcadores()

        for (unSoc in unSocList) {
            if (recorr != unSoc.recorrId) {
                agregarPolilinea(routePoints)
                routePoints = emptyList()
                recorr = unSoc.recorrId
            }
            // acumular el recorrido en transito. sera insertado al mapa cuando aparezca uno nuevo
            routePoints = routePoints.plus(geo(unSoc))
            agregarMarcador(unSoc)  // los puntos se insertan en cada pasada "mapView.overlays.add"
        }
        agregarPolilinea(routePoints)
        val startPoint = routePoints.first()
        mapController.setCenter(startPoint)
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

    private fun agregarPolilinea(routePoints: List<GeoPoint>) {
        val polyline = Polyline().apply {
            setPoints(routePoints)
            color = Color.RED
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