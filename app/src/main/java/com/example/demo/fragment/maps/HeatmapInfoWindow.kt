package com.example.demo.fragment.maps

import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebSettings
import android.webkit.WebView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import com.example.demo.R
import com.example.demo.model.UnidSocial

class HeatmapInfoWindow(
    private val context: Context,
    private val unSoc: UnidSocial,
    layoutResId: Int,
    mapView: MapView
) : InfoWindow(layoutResId, mapView) {

    override fun onOpen(item: Any?) {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_osm_heat, null)
        val webView: WebView = view.findViewById(R.id.webViewHeat)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val htmlContent = generateHeatmapHTML()
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        mView = view
    }

    override fun onClose() {
        // Actions when the info window is closed
    }

    private fun generateHeatmapHTML(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
            </head>
            <body>
                <div id="heatmap" style="width:100%;height:100%;"></div>
                <script>
                    var data = [
                        {
                            z: [
                                [1, 20, 30],
                                [20, 1, 60],
                                [30, 60, 1]
                            ],
                            type: 'heatmap',
                            colorscale: 'Viridis'
                        }
                    ];

                    var layout = {
                        title: 'Heatmap Example',
                        xaxis: {
                            title: 'X Axis'
                        },
                        yaxis: {
                            title: 'Y Axis'
                        }
                    };

                    Plotly.newPlot('heatmap', data, layout);
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}