package phocidae.mirounga.leonina.servicios

import android.content.Context
import phocidae.mirounga.leonina.R
import java.util.UUID

class ETL(private val context: Context) {

    private val idMapDia = mutableMapOf<String, UUID>()
    private val idMapRecorr = mutableMapOf<String, ArrayList<String>>()

    fun extraerDiaId(fila: Map<String, String>): UUID {
        // el discrinador de dia es facil de ver
        val idAnterior = fila["fecha"]!!
        var idNuevo = idMapDia[idAnterior]

        if (idNuevo == null) {
            idNuevo = GestorUUID.obtenerUUID()
            idMapDia[idAnterior] = idNuevo
        }
        return idNuevo
    }

    fun extraerRecorrId(fila: Map<String, String>): UUID {

        val idDia = fila["fecha"]!!

        var recorrList = idMapRecorr[idDia]
        if (recorrList == null)
            recorrList = ArrayList()

        val recorrActual = fila["libreta"]!!    // ¿cual es el discriminador de recorrido?
        var recorr = recorrList.find {
            it.split("@").firstOrNull()?.contains(recorrActual, ignoreCase = true) == true
        }

        if (recorr == null) {
            recorr = "$recorrActual@${GestorUUID.obtenerUUID()}"
            recorrList.add(recorr)
            idMapRecorr[idDia] = recorrList
        }

        val uuid = recorr.split("@")
        return UUID.fromString(uuid[1])
    }

    fun extraerUnSocId(fila: Map<String, String>): UUID {
        return GestorUUID.obtenerUUID()
    }

    fun transformarFecha(fecha: String): String {
        val fechaOficial = FechaOficial(context)
        return fechaOficial.transformar(fecha)
    }

    fun transformarLat(lat: String): Double {
        return lat.toDouble()
    }

    fun transformarLon(lon: String): Double {
        return lon.toDouble()
    }

    fun transformarPtoObservacion(): String {
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

    fun transformarSustrato(): String {
        return getOpcionesSustrato()[0]
    }

    fun transformarMarea(): String {
        return getOpcionesMarea()[0]
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

    private fun getOpcionesMarea(): Array<String> {
        return context.resources.getStringArray(R.array.op_marea)
    }

    fun ordenar(mapas: List<Map<String, String>>): List<Map<String, String>> {
        return sortCSV(mapas, "fecha", "lat0", "lon0")
    }

    private fun rellenarNulos(
        mapas: List<Map<String, String>>,
        clave: String,
        valorDefecto: String
    ): List<Map<String, String>> {
        var ultimoValorNoNulo: String = valorDefecto

        return mapas.map { mapa ->
            val nuevoMapa = mapa.toMutableMap()
            val valorActual = mapa[clave]
            if (valorActual.isNullOrEmpty()) {
                nuevoMapa[clave] = ultimoValorNoNulo
            } else {
                ultimoValorNoNulo = valorActual
            }
            nuevoMapa.toMap()
        }
    }

    private fun sortCSV(
        csvData: List<Map<String, String>>,
        primeraK: String,
        segundaK: String,
        terceraK: String
    ): List<Map<String, String>> {
        return csvData.sortedWith(compareBy({ it[primeraK] }, { it[segundaK] }, { it[terceraK] }))
    }
}
