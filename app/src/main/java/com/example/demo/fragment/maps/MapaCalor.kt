package com.example.demo.fragment.maps

import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.example.demo.exception.MagNulaExcepcion
import com.example.demo.model.UnidSocial
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint

class MapaCalor(private val webView: WebView, private val geoPoint: GeoPoint) {

    fun mostrarMapaCalor(unSocList: List<UnidSocial>, atribString: String) {
        webView.visibility = View.VISIBLE
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val htmlContent = generarHTML(unSocList, atribString)
        webView.loadDataWithBaseURL(
            "file:///android_asset/index.html",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun generarHTML(unSocList: List<UnidSocial>, atribString: String): String {

        val gson = Gson()
        val jsonUnSocList = gson.toJson(unSocList.map {

            val campo = it.javaClass.getDeclaredField(atribString)
            campo.isAccessible = true

            mapOf(
                "lat" to it.latitud,
                "lon" to it.longitud,
                "mag" to campo.get(it),
                "categ." to atribString
            )
        })

        // Parsear el JSON
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        val rows: List<Map<String, Any>> = gson.fromJson(jsonUnSocList, listType)

        // Verificar si todos los elementos tienen mag == 0
        if (rows.all { it["mag"] == 0.0 }) {
            throw MagNulaExcepcion()
        }

        val staticHtmlIni = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <script src="plotly-2.32.0.min.js"></script>
                    <style>
                        html, body {
                            width: 100%;
                            height: 100%;
                        }
                    </style>
                </head>
                <body>
                    <div id="myDiv" style="width: 100%; height: 100%;"></div>
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
                            text: unpack(rows, "categ."),
                            hoverinfo: "lat+lon+z+text",
                            hovertemplate: "lat: %{lat:.6f}<br>lon: %{lon:.6f}<br>total: %{z}<br>categ.: %{text}<extra></extra>",
                            radius: 25,
                            type: "densitymapbox",
                            coloraxis: "coloraxis"
                        }];
                        """.trimIndent()

        val staticHtmlEnd = """

                        var layout = {
                            margin: { t: 0, b: 15, l: 5 },
                            mapbox: {
                                center: {
                                    lat: ${geoPoint.latitude}, 
                                    lon: ${geoPoint.longitude} 
                                },
                                style: "carto-positron",
                                zoom: ${geoPoint.altitude}
                            },
                           coloraxis: { colorscale: 'RdBu' }
                        };
                        Plotly.newPlot("myDiv", data, layout);
                    </script>
                </body>
            </html>
        """.trimIndent()
        println(staticHtmlIni + dynamicHtml + staticHtmlEnd)
        return staticHtmlIni + dynamicHtml + staticHtmlEnd
    }
}