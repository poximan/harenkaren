package com.example.demo.fragment.maps

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.demo.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomMarkerInfoWindow(
    private val layoutResId: Int,
    mapView: MapView,
    private val snippet: String
) : InfoWindow(layoutResId, mapView) {

    override fun onOpen(item: Any?) {
        val inflater =
            mapView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(layoutResId, null)

        val txtSnippet = mView.findViewById<TextView>(R.id.snippet)

        txtSnippet.text = snippet
    }

    override fun onClose() {
        // Realizar acciones cuando se cierra la ventana de información del marcador
    }
}

