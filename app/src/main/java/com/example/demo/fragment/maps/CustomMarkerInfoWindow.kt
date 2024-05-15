package com.example.demo.fragment.maps

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.demo.R
import com.example.demo.model.UnidSocial
import org.eazegraph.lib.charts.StackedBarChart
import org.eazegraph.lib.models.BarModel
import org.eazegraph.lib.models.StackedBarModel
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomMarkerInfoWindow(
    private val context: Context,
    private val layoutResId: Int,
    private val mapView: MapView,
    private val stackchart: StackedBarChart,
    private val unSoc: UnidSocial,
    private val total: Int
) : InfoWindow(layoutResId, mapView) {

    override fun onOpen(item: Any?) {
        val inflater =
            mapView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(layoutResId, null)

        val txtSnippet = mView.findViewById<TextView>(R.id.snippet)
        txtSnippet.text = unSoc.getContadoresNoNulos().toString()

        stackchart.clearChart()
        stackchart.addBar(armarResumen(unSoc, total))
        stackchart.startAnimation()
    }

    override fun onClose() {
        // Realizar acciones cuando se cierra la ventana de información del marcador
    }

    private fun armarResumen(unidSocial: UnidSocial, total: Int): StackedBarModel {

        var acumulado = 0
        val stackBarModel = StackedBarModel()

        val contadoresNoNulos = unidSocial.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {
            acumulado += setData(stackBarModel, atribString, unidSocial)
        }
        if (total != 0)
            cerrarSet(stackBarModel, total - acumulado)

        stackBarModel.legendLabel = "id${unidSocial.orden}($acumulado)"
        return stackBarModel
    }

    private fun setData(
        stackBarModel: StackedBarModel,
        atribString: String,
        unidSocial: UnidSocial
    ): Int {

        val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = valorAtributo.get(unidSocial) as Int

        stackBarModel.addBar(BarModel(atribString, valor.toFloat(), siguienteColor(atribString)))
        return valor
    }

    private fun cerrarSet(stackBarModel: StackedBarModel, diferencia: Int) {
        stackBarModel.addBar(BarModel("vacio", diferencia.toFloat(), 0))
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

        return ContextCompat.getColor(context, coloresMap[atribString]!!)
    }
}

