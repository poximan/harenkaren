package com.example.demo.servicios

import android.content.Context
import com.example.demo.R

class ETL(private val context: Context) {

    fun transformarFecha(fecha: String): String {
        val fechaOficial = FechaOficial(context)
        return fechaOficial.transformar(fecha)
    }

    fun transformarLatLon(latlon: String?): Double {
       return if (!latlon.isNullOrEmpty())
           latlon!!.toDouble()
       else
           0.0
    }

    fun transformarPtoObservacion(referencia: String): String {
        return getOpcionesPtoObservacion()[0]
    }

    fun transformarCtxSocial(referencia: String): String {
        return when (referencia) {
            "HAREN" -> getOpcionesCtxSocial()[1]
            "HAREN SIN ALFA" -> getOpcionesCtxSocial()[2]
            "GRUPO DE HARENES" -> getOpcionesCtxSocial()[3]
            else -> getOpcionesCtxSocial()[0]
        }
    }

    fun transformarSustrato(referencia: String): String {
        return getOpcionesSustrato()[0]
    }

    private fun getOpcionesPtoObservacion(): Array<String> {
        return context.resources.getStringArray(R.array.op_punto_obs_unsoc)
    }

    private fun getOpcionesCtxSocial(): Array<String> {
        return context.resources.getStringArray(R.array.op_contexto_social)
    }

    private fun getOpcionesSustrato(): Array<String> {
        return context.resources.getStringArray(R.array.op_tipo_sustrato)
    }
}
