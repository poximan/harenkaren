package com.example.demo.servicios

import android.content.Context
import com.example.demo.R
import java.util.ArrayList
import java.util.UUID

class ETL(private val context: Context) {

    private val LAT_CENPAT = -42.785147
    private val LON_CENPAT = -65.008660
    private val idMapDia = mutableMapOf<String, UUID>()
    private val idMapRecorr = mutableMapOf<String, ArrayList<String>>()

    private var  pasadas = 0

    fun extraerDiaId(fila: Map<String, String>): UUID {
        // el discrinador de dia es facil de ver
        val idAnterior = fila["fecha"]!!
        var idNuevo = idMapDia[idAnterior]

        if (idNuevo == null) {
            idNuevo = GestorUUID.obtenerUUID(fila.toString())
            idMapDia[idAnterior] = idNuevo
        }
        return idNuevo
    }

    fun extraerRecorrId(fila: Map<String, String>): UUID {

        val idDia = fila["fecha"]!!

        var recorrList = idMapRecorr[idDia]
        if (recorrList == null)
            recorrList = ArrayList()

        val recorrActual = fila["libreta"]!!    // ¿cual es el discrinador de recorrido?
        var recorr = recorrList.find {
            it.split("@").firstOrNull()?.contains(recorrActual, ignoreCase = true) == true
        }

        pasadas++
        if(recorr == null) {
            recorr = "$recorrActual@${GestorUUID.obtenerUUID("")}"
            recorrList.add(recorr)
            idMapRecorr[idDia] = recorrList
        }

        val uuid = recorr.split("@")
        return UUID.fromString(uuid[1])
    }

    fun extraerUnSocId(fila: Map<String, String>): UUID {
        return GestorUUID.obtenerUUID("")
    }

    fun transformarFecha(fecha: String): String {
        val fechaOficial = FechaOficial(context)
        return fechaOficial.transformar(fecha)
    }

    fun transformarLat(latlon: String?): Double {
       return if (!latlon.isNullOrEmpty())
           latlon.toDouble()
       else
           LAT_CENPAT
    }

    fun transformarLon(latlon: String?): Double {
        return if (!latlon.isNullOrEmpty())
            latlon.toDouble()
        else
            LON_CENPAT
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

    fun ordenar(mapas: List<Map<String, String>>): List<Map<String, String>> {
        return sortCSV(mapas, "lat0", "lon0")
    }

    private fun sortCSV(csvData: List<Map<String, String>>, primaryKey: String, secondaryKey: String): List<Map<String, String>> {
        return csvData.sortedWith(compareBy({ it[primaryKey] }, { it[secondaryKey] }))
    }
}
