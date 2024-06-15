package com.example.demo.fragment.statistics

import android.webkit.WebSettings
import android.webkit.WebView
import com.example.demo.model.UnidSocial
import com.google.gson.Gson
import org.osmdroid.util.GeoPoint

class ReporteMapa(private val webView: WebView, private val geoPoint: GeoPoint) {

    fun mostrarMapaCalor(unSocList: List<UnidSocial>) {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val htmlContent = generarHTML(unSocList)
        webView.loadDataWithBaseURL(
            "file:///android_asset/index.html",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun generarHTML(unSocList: List<UnidSocial>): String {

        val gson = Gson()
        val jsonUnSocList = gson.toJson(unSocList.map {
            mapOf(
                "lat" to "-" + it.latitud.toString().substring(1),
                "lon" to it.longitud,
                "mag" to it.vHembrasAd + it.vCrias,
                "suma" to "vHembrasAd+vCrias"
            )
        })

        val staticHtmlIni = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <script src="plotly-2.32.0.min.js"></script>
                </head>
                <body>
                    <div id="myDiv"></div>
                    """.trimIndent()

        val dynamicHtml = """
            
                    <script>
                        function unpack(rows, key) {
                            return rows.map(function (row) {
                                return row[key];
                            });
                        }
                        var rows = $jsonUnSocList;
                    
                        var data = [{
                            lat: unpack(rows, "lat"),
                            lon: unpack(rows, "lon"),
                            z: unpack(rows, "mag"),
                            text: unpack(rows, "suma"),
                            hoverinfo: "lat+lon+z+text",
                            hovertemplate: "lat: %{lat:.6f}<br>lon: %{lon:.6f}<br>suma: %{z}<br>%{text}<extra></extra>",
                            radius: 25,
                            type: "densitymapbox",
                            coloraxis: "coloraxis"
                        }];
                        """.trimIndent()

        val staticHtmlEnd = """

                        var layout = {
                            margin: { t: 0, r: 0, b: 0, l: 0 },
                            mapbox: {
                                center: {
                                    lat: ${geoPoint.latitude}, 
                                    lon: ${geoPoint.longitude} 
                                },
                                style: "carto-positron",
                                zoom: ${geoPoint.altitude},
                            },
                           coloraxis: { colorscale: 'RdBu' }
                        };
                        var config = {
                            responsive: true,
                            displayModeBar: false
                        };
                        Plotly.newPlot("myDiv", data, layout, config);
                    </script>
                </body>
            </html>
        """.trimIndent()
        return staticHtmlIni + dynamicHtml + staticHtmlEnd
    }
}