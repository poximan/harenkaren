package com.example.demo.fragment.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import com.example.demo.R
import com.example.demo.activity.HomeActivity
import com.example.demo.exception.MagNulaExcepcion
import com.google.android.material.navigation.NavigationView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class CalorFragment : SuperMapa() {

    private lateinit var mapView: MapView
    private lateinit var webView: WebView

    // Método llamado cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        webView = view.findViewById(R.id.webViewHeat)
        mapView = view.findViewById(R.id.mapView)
        webView.visibility = View.VISIBLE
        mapView.visibility = View.INVISIBLE

        filtroAnio = view.findViewById(R.id.filtroAnio)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_drawer_menu)
    }

    override fun resolverVisibilidad() {
        val geoPoint: GeoPoint = puntoMedioPosiciones(unSocList)
        geoPoint.altitude -= 2

        val mapaCalor = MapaCalor(webView, geoPoint)

        val context = requireContext()
        try {
            mapaCalor.mostrarMapaCalor(unSocList, selectedRadioButton!!.text.toString())
        } catch (e: NullPointerException) {
            Toast.makeText(
                context,
                context.getString(R.string.osm_categoria),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: MagNulaExcepcion) {
            Toast.makeText(
                context,
                context.getString(R.string.osm_categNula),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}