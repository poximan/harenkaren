package com.example.demo.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocListGrafBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class UnSocListGrafFragment : Fragment() {

    private val args: UnSocListFragmentArgs by navArgs()

    private var _binding: FragmentUnsocListGrafBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocListGrafBinding.inflate(inflater, container, false)

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.newUnsocButton.setOnClickListener { nuevaUnidadSocial() }
        binding.cambiarActionButton.setOnClickListener { cambiarVista() }

        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true

        val fileName = "index.html"
        val file = File(
            requireContext().filesDir,
            fileName
        ) // Obtiene la referencia al directorio de archivos de la aplicación

        if (!file.exists()) {
            file.createNewFile()
        }

        val fileOutputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)

        val dynamicHtml = generateDynamicHtml()
        outputStreamWriter.write(dynamicHtml)
        outputStreamWriter.close()

        // Cargar el archivo HTML en el WebView
        webView.loadUrl("file:///${file.absolutePath}")

        return binding.root
    }

    private fun generateDynamicHtml(): String {

        val staticHtmlIni = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Plotly Stacked Horizontal Bar Chart</title>
                <script src="file:///android_asset/plotly-2.32.0.min.js"></script>
            </head>
            <body>
            <div id="chart" style="width: 100%; height: 100%;"></div>
            <script>
        """.trimIndent()

        // Parte dinámica del HTML (trazas)
        val (traces, data) = generateTraces()
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
                    title: 'Stacked Horizontal Bar Chart'
                };

                Plotly.newPlot('chart', data, layout);
            </script>
            </body>
            </html>
        """.trimIndent()

        // Combinar partes estáticas y dinámicas
        return staticHtmlIni + dynamicHtml.toString() + dataStr + staticHtmlEnd
    }

    private fun generateTraces(): Pair<List<String>, String> {
        val traceList = mutableListOf<String>()
        val dataList = mutableListOf<String>()

        // Generar 24 trazas
        for (i in 1..24) {
            val trace = """
                
            var trace$i = {
                x: [20, 14, 23],
                y: ['Category A', 'Category B', 'Category C'],
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

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nuevaUnidadSocial() {
        var action = UnSocListFragmentDirections.goToNewUnSocFromUnSocListAction(args.idRecorrido)
        findNavController().navigate(action)
    }

    private fun cambiarVista() {
        var action = UnSocListGrafFragmentDirections.goToModoTexto(args.idRecorrido)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }
}