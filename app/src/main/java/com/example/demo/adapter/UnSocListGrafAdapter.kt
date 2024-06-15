package com.example.demo.adapter

import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.NoSuchElementException
import java.util.UUID

class UnSocListGrafAdapter(private val idRecorrido: UUID) {

    suspend fun contenidoHTML(unSocViewModel: UnSocViewModel): String {
        val unSocListAsync = withContext(Dispatchers.IO) {
            unSocViewModel.readConFK(idRecorrido)
        }
        return withContext(Dispatchers.Main) {
            if(unSocListAsync.isEmpty())
                throw NoSuchElementException()

            generarHTML(unSocListAsync)
        }
    }

    private fun generarHTML(unidSocialList: List<UnidSocial>): String {

        val staticHtmlIni = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <script src="file:///android_asset/plotly-2.32.0.min.js"></script>
                    <style>
                        body, html {                            
                            width: 100%;
                            height: 92%;
                            overflow: hidden;
                        }
                    </style>
                </head>
                <body>
                    <div id="chart" style="width: 100%; height: 100%;"></div>
                    <script>
                    """.trimIndent()

        // Parte dinámica del HTML (trazas)
        val (traces, data, yval) = generarCategorias(unidSocialList)
        val dynamicHtml = StringBuilder()
        for (trace in traces) {
            dynamicHtml.append(trace).append("\n")
        }

        val dataStr = """
            var data = [$data]
            """.trimIndent()

        // Parte estática final del HTML
        val staticHtmlEnd = """

                        var layout = {
                            margin: { t: 5, r: 155, b: 0, l: 10 },
                            barmode: 'stack',
                            yaxis: {
                                tickvals: $yval
                            }
                        };
                        var config = {
                            responsive: true,
                            displayModeBar: false
                        };
                        Plotly.newPlot('chart', data, layout, config);
                        
                    </script>
                </body>
            </html>
        """.trimIndent()

        // Combinar partes estáticas y dinámicas
        return staticHtmlIni + dynamicHtml.toString() + dataStr + staticHtmlEnd
    }

    private fun generarCategorias(unidSocialList: List<UnidSocial>): Triple<List<String>, String, String> {

        val traceList = mutableListOf<String>()
        val dataList = mutableListOf<String>()

        var yval = ""
        val contadores = unidSocialList.first().getContadores()
        for (atribString in contadores) {

            var xval = ""
            yval = ""
            var xtext = ""

            for (unidSocial in unidSocialList) {
                val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
                valorAtributo.isAccessible = true
                val valor = valorAtributo.get(unidSocial)

                xval += "'$valor', "
                yval += "'${unidSocial.orden}', "
                xtext += "'$atribString', "
            }
            xval = xval.dropLast(2) // sacar el ultimo ", "
            xval = "[$xval]"

            yval = yval.dropLast(2)
            yval = "[$yval]"

            xtext = xtext.dropLast(2)
            xtext = "[$xtext]"

            val trace = """
                
            var $atribString = {
                x: $xval,
                y: $yval,
                text: $xtext,
                textangle: 90,
                name: '$atribString',
                "orientation": "h",
                type: 'bar',
                hovertemplate: 'reg n°%{y}<br>%{text}=%{x}'
            };
        """.trimIndent()
            val data = "$atribString"
            traceList.add(trace)
            dataList.add(data)
        }
        return Triple(traceList, dataList.joinToString(", "), yval)
    }
}