package com.example.demo.fragment.statistics

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.dao.UnSocDAO
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentReportesBinding
import com.example.demo.model.UnidSocial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import kotlin.math.abs

class ReportesFragment : Fragment() {

    private lateinit var unSocDAO: UnSocDAO

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private val args: ReportesFragmentArgs by navArgs()

    private lateinit var scrollView: ScrollView
    private lateinit var webViewHeat: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)

        scrollView = binding.scrollReporte
        webViewHeat = binding.webViewHeat

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Controla el scroll del ScrollView
        webViewHeat.setOnTouchListener { _, _ ->
            // Deshabilita el scroll del ScrollView mientras se está interactuando con el WebView
            scrollView.requestDisallowInterceptTouchEvent(true)
            false
        }

        scrollView.setOnTouchListener { _, _ ->
            // Habilita el scroll del ScrollView cuando no se está interactuando con el WebView
            scrollView.requestDisallowInterceptTouchEvent(false)
            false
        }

        binding.imprimirPdf.setOnClickListener {
            printPdf()
        }

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        getInvolucrados(args.rangoFechas) {
            if(it.isNotEmpty()){
                participantes(it)
                completarMapa(it)
                contarCrias(it)
                tabFilaHaren(it)
                tabFilaGpoHaren(it)
                tabFilaHarenSin(it)
                tabFilaPjaSolit(it)
                tabFilaIndivSolo(it)
            }else{
                val mensaje = getString(R.string.rep_rangoVacio) + " " + args.rangoFechas
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun participantes(unSocList: List<UnidSocial>) {
        binding.participantes.text = " " + unSocList.distinctBy { it.recorrId }.size.toString()
    }

    private fun completarMapa(unSocList: List<UnidSocial>) {
        val geoPoint: GeoPoint = puntoMedioPosiciones(unSocList)
        val mapaCalor = ReporteMapa(webViewHeat, geoPoint)
        mapaCalor.mostrarMapaCalor(unSocList)
    }

    private fun contarCrias(unSocList: List<UnidSocial>) {
        val vCrias = unSocList.sumOf { it.vCrias }
        val mCrias = unSocList.sumOf { it.mCrias }

        binding.crias.text = " $vCrias(vivas) $mCrias(muertas)"
    }

    private fun tabFilaHaren(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_haren)
        }
        binding.tablePart.totHaren.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_haren) }.sumOf {
            it.vHembrasAd }
        binding.tablePart.promHaren.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            getString(R.string.rep_tabNoAplica)
        }
    }

    private fun tabFilaGpoHaren(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_gpoHarenes)
        }
        binding.tablePart.totGpoharen.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_gpoHarenes) }.sumOf {
            it.vHembrasAd }
        binding.tablePart.promGpoharen.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            getString(R.string.rep_tabNoAplica)
        }
    }

    private fun tabFilaHarenSin(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_harenSAlfa)
        }
        binding.tablePart.totHarensin.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_harenSAlfa) }.sumOf {
            it.vHembrasAd }
        binding.tablePart.promHarensin.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            getString(R.string.rep_tabNoAplica)
        }
    }

    private fun tabFilaPjaSolit(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_pjaSolitaria)
        }
        binding.tablePart.totPjasolit.text = total.toString()
    }

    private fun tabFilaIndivSolo(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_indivSolo)
        }
        binding.tablePart.totIndivsolo.text = total.toString()
    }

    private fun promediarPosiciones(unSocList: List<UnidSocial>): GeoPoint {
        val totalLatitudes = unSocList.sumOf { it.latitud }
        val totalLongitudes = unSocList.sumOf { it.longitud }

        val promedioLatitud = totalLatitudes / unSocList.size
        val promedioLongitud = totalLongitudes / unSocList.size

        return GeoPoint(promedioLatitud, promedioLongitud)
    }

    private fun puntoMedioPosiciones(unSocList: List<UnidSocial>): GeoPoint {
        val minLatitud = unSocList.minOf { it.latitud }
        val maxLatitud = unSocList.maxOf { it.latitud }
        val minLongitud = unSocList.minOf { it.longitud }
        val maxLongitud = unSocList.maxOf { it.longitud }

        // Calcular los puntos medios respecto a los valores extremos
        val puntoMedioLatitud = (minLatitud + maxLatitud) / 2.0
        val puntoMedioLongitud = (minLongitud + maxLongitud) / 2.0

        val altitud = -1.9481 * abs(minLatitud-maxLatitud) + 9.5195
        /*
        para 0.78 dif lat --> 8 altitud
        para 1.55 dif lat --> 6.5 altitud
         */
        return GeoPoint(puntoMedioLatitud, puntoMedioLongitud, altitud)
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

    private fun printPdf() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfPrintDocumentAdapter(requireContext(), scrollView)
        val jobName = "${requireContext().applicationInfo.loadLabel(requireContext().packageManager)} Document"

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }
}