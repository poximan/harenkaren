package com.example.demo.fragment.statistics

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.demo.dao.UnSocDAO
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentReportesBinding
import com.example.demo.model.UnidSocial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

class ReportesFragment : Fragment() {

    private lateinit var unSocDAO: UnSocDAO

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private val args: ReportesFragmentArgs by navArgs()

    private lateinit var webViewHeat: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)

        webViewHeat = binding.webViewHeat
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imprimirPdf.setOnClickListener {
            printPdf()
        }

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        getInvolucrados(args.rangoFechas) {
            if(it.isNotEmpty())
                completarMapa(it)
        }
    }

    private fun printPdf() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfPrintDocumentAdapter(requireContext(), binding.scrollReporte)
        val jobName = "${requireContext().applicationInfo.loadLabel(requireContext().packageManager)} Document"

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun completarMapa(unSocList: List<UnidSocial>) {
        val geoPoint: GeoPoint = promediarPosiciones(unSocList)
        val mapaCalor = ReporteMapa(webViewHeat, geoPoint)
        mapaCalor.mostrarMapaCalor(unSocList)
    }

    private fun promediarPosiciones(unSocList: List<UnidSocial>): GeoPoint {
        val totalLatitudes = unSocList.sumOf { it.latitud }
        val totalLongitudes = unSocList.sumOf { it.longitud }

        val promedioLatitud = totalLatitudes / unSocList.size
        val promedioLongitud = totalLongitudes / unSocList.size

        return GeoPoint(promedioLatitud, promedioLongitud)
    }

    private fun getInvolucrados(rangoFechas: String, callback: (List<UnidSocial>) -> Unit) {
        var unSocList: List<UnidSocial>
        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            unSocList = unSocDAO.getEntreFechas(
                rangoFechas.split(" ")[0],
                rangoFechas.split(" ")[1]
            )

            // ---------> HILO PRINCIPAL
            withContext(Dispatchers.Main) {
                callback(unSocList)
            }
        }
    }
}