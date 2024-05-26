package com.example.demo.fragment.maps

import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

class MapaCalor(private val webView: WebView) {

    fun mostrarMapaCalor() {
        webView.visibility = View.VISIBLE
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val htmlContent = generateMapboxDensityHTML()
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun generateMapboxDensityHTML(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
            	<!-- Load plotly.js into the DOM -->
            	<script src="file:///android_asset/plotly-2.32.0.min.js"></script>
            	<script src="file:///android_asset/d3-3.5.17.min.js"></script>
            <script>
                d3.csv(
                  "https://raw.githubusercontent.com/plotly/datasets/master/earthquakes-23k.csv",
                  function (err, rows) {
                    function unpack(rows, key) {
                      return rows.map(function (row) {
                        return row[key];
                      });
                    }
    
                    var data = [
                      {
                        lon: unpack(rows, "Longitude"),
                        lat: unpack(rows, "Latitude"),
                        radius: 10,
                        z: unpack(rows, "Magnitude"),
                        type: "densitymapbox",
                        coloraxis: "coloraxis",
                        hoverinfo: "skip"
                      }
                    ];
    
                    var layout = {
                      mapbox: {
                        center: { lon: 60, lat: 30 },
                        style: "open-street-map",
                        zoom: 2
                      },
                      coloraxis: { colorscale: "Viridis" },
                      title: { text: "Earthquake Magnitude" },
                    };  
                    Plotly.newPlot("myDiv", data, layout);
                  }
                );
            </script>
            </head>

            <body>
            	<div id='myDiv'><!-- Plotly chart will be drawn inside this DIV --></div>
            </body>
            </html>
        """.trimIndent()
    }
}