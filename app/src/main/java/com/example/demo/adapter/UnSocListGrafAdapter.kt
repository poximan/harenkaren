package com.example.demo.adapter

import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class UnSocListGrafAdapter(private val idRecorrido: UUID) {

    suspend fun contenidoHTML(unSocViewModel: UnSocViewModel): String {
        val unSocListAsync = withContext(Dispatchers.IO) {
            unSocViewModel.readConFK(idRecorrido)
        }
        return withContext(Dispatchers.Main) {
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
            </head>
            <body>
            <div id="chart" style="width: 100%; height: 100%;"></div>
            <script>
        """.trimIndent()

        // Parte dinámica del HTML (trazas)
        val (traces, data) = generarCategorias(unidSocialList)
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
                    barmode: 'stack',
                };
                Plotly.newPlot('chart', data, layout);
            </script>
            </body>
            </html>
        """.trimIndent()

        // Combinar partes estáticas y dinámicas
        return staticHtmlIni + dynamicHtml.toString() + dataStr + staticHtmlEnd
    }

    private fun generarCategorias(unidSocialList: List<UnidSocial>): Pair<List<String>, String> {

        for (unidSocial in unidSocialList) {
            val contadores = unidSocial.getContadores()
            for (atribString in contadores) {
                // obtengo un objeto Field
                val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
                valorAtributo.isAccessible = true
                // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
                val valor = valorAtributo.get(unidSocial)
            }
        }

        val traceList = mutableListOf<String>()
        val dataList = mutableListOf<String>()

        // Generar 24 trazas
        for (i in 1..24) {
            val trace = """
                
            var trace$i = {
                x: [20, 14, 23, 31, 8, 15],
                y: ['reg6', 'reg5', 'reg4','reg3', 'reg2', 'reg1'],
                text: ['vJuveniles', 'vDestetados', 'vCrias', 'vHembrasAd', 'vAlfaSams', 'vBeta'],
                name: 'Trace $i',
                orientation: 'h',
                type: 'bar'
            };
        """.trimIndent()
            val data = "trace$i"
            traceList.add(trace)
            dataList.add(data)
        }
        return Pair(traceList, dataList.joinToString(", "))
    }
}