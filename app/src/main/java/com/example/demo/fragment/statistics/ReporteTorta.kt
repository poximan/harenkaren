package com.example.demo.fragment.statistics

import android.webkit.WebView
import com.example.demo.model.UnidSocial
import com.google.gson.Gson
import org.osmdroid.util.GeoPoint

class ReporteTorta(private val webView: WebView) {

    fun mostrarMapaCalor(unSocList: List<UnidSocial>) {

        val htmlContent = generarHTML(unSocList)
        println(htmlContent)
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
                "valores" to it.vAlfaS4Ad,
                "categorias" to it.getContadores()
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
                            values: unpack(rows, "valores"),
                            labels: unpack(rows, "categorias"),                                                                                
                            type: "pie"                            
                        }];
                        """.trimIndent()

        val staticHtmlEnd = """

                        var layout = {
                            margin: { t: 0, r: 0, b: 0, l: 0 },
                            showlegend: true
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