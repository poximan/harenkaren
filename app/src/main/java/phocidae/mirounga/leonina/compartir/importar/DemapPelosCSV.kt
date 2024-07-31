package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import phocidae.mirounga.leonina.model.EntidadesPlanas
import phocidae.mirounga.leonina.servicios.ETL

class DemapPelosCSV(context: Context) : Desmapeable {

    private val etl = ETL(context)

    override fun desmapear(entidades: List<Map<String, String>>): List<EntidadesPlanas> {

        val listaEntidades = mutableListOf<EntidadesPlanas>()

        val mapasOrd = etl.ordenar(entidades)

        val ptoObs = etl.transformarPtoObservacion()
        val sustrato = etl.transformarSustrato()
        val marea = etl.transformarMarea()

        for (map in mapasOrd) {

            if (map["lat0"] == "" || map["lon0"] == "")
                continue

            val diaId = etl.extraerDiaId(map)
            val recorrId = etl.extraerRecorrId(map)
            val unidSocId = etl.extraerUnSocId(map)

            val fechaTransformada = etl.transformarFecha(map["fecha"]!!)
            val lat0 = etl.transformarLat(map["lat0"]!!)
            val lon0 = etl.transformarLon(map["lon0"]!!)
            val ctxSocial = etl.transformarCtxSocial(map["referencia"]!!)

            val entidadPlanta = EntidadesPlanas(
                "cel_no_aplica",
                diaId,
                map["orden"]!!.toInt(),
                fechaTransformada.substringBefore(" "),
                recorrId,
                diaId,
                map["orden"]!!.toInt(),
                "observador_desc",
                fechaTransformada,
                fechaTransformada,
                lat0 + 0.004,
                lon0 + 0.004,
                lat0 + 0.004,
                lon0 + 0.004,
                map["playa"]!!,
                "meteo_desc",
                marea,
                map["tipo"]!!,
                unidSocId,
                recorrId,
                map["orden"]!!.toInt(),
                ptoObs,
                ctxSocial,
                sustrato,
                map["machosContados"]!!.toInt(),
                0,
                map["hembrasContadas"]!!.toInt(),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                fechaTransformada,
                lat0,
                lon0,
                map["tipo"]!!
            )
            listaEntidades.add(entidadPlanta)
        }
        return listaEntidades
    }
}