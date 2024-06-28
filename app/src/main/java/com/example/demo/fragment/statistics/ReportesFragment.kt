package com.example.demo.fragment.statistics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.osmdroid.util.GeoPoint
import java.util.UUID
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
        webViewHeat = binding.webViewRep

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

        binding.fijarCuadro.setOnClickListener {
            fijarCuadro()
        }

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        getInvolucrados(args.rangoFechas) {
            if (it.isNotEmpty()) {
                participantes(it)
                completarMapa(it)
                contarCrias(it)
                tabFilaHaren(it)
                tabFilaGpoHaren(it)
                tabFilaHarenSin(it)
                tabFilaPjaSolit(it)
                tabFilaIndivSolo(it)
                val reduccion = reducir(it)
                graficar(reduccion)
            } else {
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

    private fun fijarCuadro() {
        webViewHeat.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                capturarContenidoWebView(webViewHeat) { bitmap ->
                    binding.imgMapaCalor.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun capturarContenidoWebView(webView: WebView, callback: (Bitmap) -> Unit) {
        webView.evaluateJavascript("html2canvas(document.body).then(canvas => canvas.toDataURL('image/png')).then(dataURL => dataURL);") { dataURL ->
            val base64Data = dataURL.removePrefix("\"data:image/png;base64,").removeSuffix("\"")
            val decodedString = Base64.decode(base64Data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            callback(bitmap)
        }
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
            it.ctxSocial == getString(R.string.ctx_haren)
        }.sumOf {
            it.vHembrasAd
        }
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
            it.ctxSocial == getString(R.string.ctx_gpoHarenes)
        }.sumOf {
            it.vHembrasAd
        }
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
            it.ctxSocial == getString(R.string.ctx_harenSAlfa)
        }.sumOf {
            it.vHembrasAd
        }
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

    private fun reducir(unSocList: List<UnidSocial>): UnidSocial {
        val randomQueTeTiro = UUID.randomUUID()
        return unSocList.reduce { acc, unidSocial ->
            UnidSocial(
                vAlfaS4Ad = acc.vAlfaS4Ad + unidSocial.vAlfaS4Ad,
                vAlfaSams = acc.vAlfaSams + unidSocial.vAlfaSams,
                vHembrasAd = acc.vHembrasAd + unidSocial.vHembrasAd,
                vCrias = acc.vCrias + unidSocial.vCrias,
                vDestetados = acc.vDestetados + unidSocial.vDestetados,
                vJuveniles = acc.vJuveniles + unidSocial.vJuveniles,
                vS4AdPerif = acc.vS4AdPerif + unidSocial.vS4AdPerif,
                vS4AdCerca = acc.vS4AdCerca + unidSocial.vS4AdCerca,
                vS4AdLejos = acc.vS4AdLejos + unidSocial.vS4AdLejos,
                vOtrosSamsPerif = acc.vOtrosSamsPerif + unidSocial.vOtrosSamsPerif,
                vOtrosSamsCerca = acc.vOtrosSamsCerca + unidSocial.vOtrosSamsCerca,
                vOtrosSamsLejos = acc.vOtrosSamsLejos + unidSocial.vOtrosSamsLejos,
                mAlfaS4Ad = acc.mAlfaS4Ad + unidSocial.mAlfaS4Ad,
                mAlfaSams = acc.mAlfaSams + unidSocial.mAlfaSams,
                mHembrasAd = acc.mHembrasAd + unidSocial.mHembrasAd,
                mCrias = acc.mCrias + unidSocial.mCrias,
                mDestetados = acc.mDestetados + unidSocial.mDestetados,
                mJuveniles = acc.mJuveniles + unidSocial.mJuveniles,
                mS4AdPerif = acc.mS4AdPerif + unidSocial.mS4AdPerif,
                mS4AdCerca = acc.mS4AdCerca + unidSocial.mS4AdCerca,
                mS4AdLejos = acc.mS4AdLejos + unidSocial.mS4AdLejos,
                mOtrosSamsPerif = acc.mOtrosSamsPerif + unidSocial.mOtrosSamsPerif,
                mOtrosSamsCerca = acc.mOtrosSamsCerca + unidSocial.mOtrosSamsCerca,
                mOtrosSamsLejos = acc.mOtrosSamsLejos + unidSocial.mOtrosSamsLejos,
                id = randomQueTeTiro, idRecorrido = randomQueTeTiro,
                ptoObsUnSoc = "", ctxSocial = "", tpoSustrato = "", timeStamp = "",
                latitud = 0.0, longitud = 0.0, photoPath = "", comentario = ""
            )
        }
    }

    private fun graficar(unidSocial: UnidSocial) {
        val pieChart: PieChart = binding.piechart
        pieChart.clearChart()

        val contadoresNoNulos = unidSocial.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {
            asignarValorPorReflexion(unidSocial, atribString)
            pieChart.addPieSlice(setData(unidSocial, atribString))
        }
        pieChart.startAnimation()
    }

    private fun asignarValorPorReflexion(unidSocial: UnidSocial, atribString: String) {

        // Genera el nombre del campo correspondiente al componente visual
        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "txt${capitalizar}${atribString.substring(1)}"

        // Obtiene el campo del binding utilizando reflexión
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        field.isAccessible = true

        if (field.type == TextView::class.java) {
            val textView = field.get(binding) as TextView

            // obtengo un objeto Field
            val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
            valorAtributo.isAccessible = true
            // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
            val valor = valorAtributo.get(unidSocial)
            textView.text = "$atribString: $valor"

            val layoutExiste = textView.parent as LinearLayout
            layoutExiste.visibility = View.VISIBLE
        }
    }

    private fun setData(unidSocial: UnidSocial, atribString: String): PieModel {
        val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = (valorAtributo.get(unidSocial) as Int).toFloat()
        return PieModel(atribString, valor, siguienteColor(atribString))
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

        val altitud = -1.9481 * abs(minLatitud - maxLatitud) + 9.5195
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
        val jobName =
            "${requireContext().applicationInfo.loadLabel(requireContext().packageManager)} Document"

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    private fun siguienteColor(atribString: String): Int {
        val coloresMap = mapOf(
            "vAlfaS4Ad" to R.color.clr_v_alfa_s4ad,
            "vAlfaSams" to R.color.clr_v_alfa_sams,
            "vHembrasAd" to R.color.clr_v_hembras_ad,
            "vCrias" to R.color.clr_v_crias,
            "vDestetados" to R.color.clr_v_destetados,
            "vJuveniles" to R.color.clr_v_juveniles,
            "vS4AdPerif" to R.color.clr_v_s4ad_perif,
            "vS4AdCerca" to R.color.clr_v_s4ad_cerca,
            "vS4AdLejos" to R.color.clr_v_s4ad_lejos,
            "vOtrosSamsPerif" to R.color.clr_v_otros_sams_perif,
            "vOtrosSamsCerca" to R.color.clr_v_otros_sams_cerca,
            "vOtrosSamsLejos" to R.color.clr_v_otros_sams_lejos,
            "mAlfaS4Ad" to R.color.clr_m_alfa_s4ad,
            "mAlfaSams" to R.color.clr_m_alfa_sams,
            "mHembrasAd" to R.color.clr_m_hembras_ad,
            "mCrias" to R.color.clr_m_crias,
            "mDestetados" to R.color.clr_m_destetados,
            "mJuveniles" to R.color.clr_m_juveniles,
            "mS4AdPerif" to R.color.clr_m_s4ad_perif,
            "mS4AdCerca" to R.color.clr_m_s4ad_cerca,
            "mS4AdLejos" to R.color.clr_m_s4ad_lejos,
            "mOtrosSamsPerif" to R.color.clr_m_otros_sams_perif,
            "mOtrosSamsCerca" to R.color.clr_m_otros_sams_cerca,
            "mOtrosSamsLejos" to R.color.clr_m_otros_sams_lejos
        )
        return ContextCompat.getColor(requireContext(), coloresMap[atribString]!!)
    }
}
