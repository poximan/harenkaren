package phocidae.mirounga.leonina.fragment.reporte

import android.webkit.WebView
import phocidae.mirounga.leonina.model.UnidSocial

class ReporteTorta(private val webView: WebView) {

    fun mostrarMapaCalor(unidSocial: UnidSocial) {

        val htmlContent = generarHTML(unidSocial)
        webView.loadDataWithBaseURL(
            "file:///android_asset/indexRepTorta.html",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun generarHTML(unidSocial: UnidSocial): String {

        val parValores = generarCategorias(unidSocial)

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
                        var data = [{
                            values: ${parValores.first},
                            labels: ${parValores.second},
                            type: "pie",
                            hole: 0.2,
                            textinfo: "label+value",
                            hoverinfo: "label+value+percent",
                            hovertemplate: "%{label}: %{value} (%{percent})",
                            textposition: 'inside'
                        }];
                        """.trimIndent()

        val staticHtmlEnd = """

                        var layout = {
                            margin: { t: 0, r: 0, b: 8, l: 0 },
                            legend: {
                                orientation: 'h',
                                font: {
                                    size: 10
                                }
                            }
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

    private fun generarCategorias(unidSocial: UnidSocial): Pair<String, String> {

        var values = ""
        var labels = ""

        val contadores = unidSocial.getContadoresNoNulos()
        for (atribString in contadores) {

            val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
            valorAtributo.isAccessible = true
            val valor = valorAtributo.get(unidSocial)

            values += "$valor, "
            labels += "'$atribString', "
        }
        values = values.dropLast(2) // sacar el ultimo ", "
        values = "[$values]"

        labels = labels.dropLast(2)
        labels = "[$labels]"

        return Pair(values, labels)
    }
}