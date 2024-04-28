package com.example.demo.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.demo.R
import com.example.demo.model.UnidSocial
import org.eazegraph.lib.charts.StackedBarChart
import org.eazegraph.lib.models.BarModel
import org.eazegraph.lib.models.StackedBarModel

class UnSocListGrafAdapter(private val context: Context) {

    internal fun setUnSoc(unidSocialList: List<UnidSocial>, stackchart: StackedBarChart, total: Int) {

        stackchart.clearChart()
        for (unidSocial in unidSocialList) {
            stackchart.addBar(armarResumen(unidSocial, total))
        }
        stackchart.startAnimation()
    }

    private fun armarResumen(unidSocial: UnidSocial, total: Int): StackedBarModel {

        var acumulado = 0
        val barModel = StackedBarModel()

        val contadoresNoNulos = unidSocial.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {
            acumulado += setData(barModel, atribString, unidSocial)
        }
        if(total != 0)
            cerrarSet(barModel, total-acumulado)

        barModel.legendLabel = "id${unidSocial.contadorInstancias}($acumulado)"
        return barModel
    }

    private fun setData(
        barModel: StackedBarModel,
        atribString: String,
        unidSocial: UnidSocial
    ): Int {

        val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = valorAtributo.get(unidSocial) as Int

        barModel.addBar(BarModel(atribString, valor.toFloat(), siguienteColor(atribString)))
        return valor
    }

    private fun cerrarSet(barModel: StackedBarModel, diferencia: Int) {
        barModel.addBar(BarModel("vacio", diferencia.toFloat(), 0))
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