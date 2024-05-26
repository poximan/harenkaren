package com.example.demo.fragment.maps

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class HeatmapOverlay_caserito(
    private val points: List<GeoPoint>
) : Overlay() {

    override fun draw(c: Canvas, osmv: MapView, shadow: Boolean) {
        if (shadow) return

        val paint = Paint().apply {
            color = Color.RED
            alpha = 50
        }

        for (point in points) {
            val proj = osmv.projection.toPixels(point, null)
            c.drawCircle(proj.x.toFloat(), proj.y.toFloat(), 20f, paint)
        }
    }
}