package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import phocidae.mirounga.leonina.model.EntidadesPlanas
import phocidae.mirounga.leonina.servicios.ETL
import java.util.UUID

class DemapKarenLegadoCSV(context: Context) : Desmapeable {

    private val etl = ETL(context)

    override fun desmapear(entidades: List<Map<String, String>>): List<EntidadesPlanas> {
        val listaEntidades = mutableListOf<EntidadesPlanas>()

        val marea = etl.transformarMarea()

        for (map in entidades) {

            val entidadPlanta = EntidadesPlanas(
                map["celular_id"]!!,
                UUID.fromString(map["dia_id"]),
                dia_orden = 0,
                map["dia_fecha"]!!,
                UUID.fromString(map["recorr_id"]),
                UUID.fromString(map["dia_id"]),
                recorr_orden = 0,
                map["observador"]!!,
                map["recorr_fecha_ini"]!!,
                map["recorr_fecha_fin"]!!,
                map["recorr_latitud_ini"]!!.toDouble(),
                map["recorr_longitud_ini"]!!.toDouble(),
                map["recorr_latitud_fin"]!!.toDouble(),
                map["recorr_longitud_fin"]!!.toDouble(),
                map["area_recorrida"]!!,
                map["meteo"]!!,
                marea,
                observaciones = "observ_desc",
                UUID.fromString(map["unidsocial_id"]),
                UUID.fromString(map["recorr_id"]),
                unsoc_orden = 0,
                map["pto_observacion"]!!,
                map["ctx_social"]!!,
                map["tpo_sustrato"]!!,
                map["v_alfa_s4ad"]!!.toInt(),
                map["v_alfa_sams"]!!.toInt(),
                map["v_hembras_ad"]!!.toInt(),
                map["v_crias"]!!.toInt(),
                map["v_destetados"]!!.toInt(),
                map["v_juveniles"]!!.toInt(),
                map["v_s4ad_perif"]!!.toInt(),
                map["v_s4ad_cerca"]!!.toInt(),
                map["v_s4ad_lejos"]!!.toInt(),
                map["v_otros_sams_perif"]!!.toInt(),
                map["v_otros_sams_cerca"]!!.toInt(),
                map["v_otros_sams_lejos"]!!.toInt(),
                map["m_alfa_s4ad"]!!.toInt(),
                map["m_alfa_sams"]!!.toInt(),
                map["m_hembras_ad"]!!.toInt(),
                map["m_crias"]!!.toInt(),
                map["m_destetados"]!!.toInt(),
                map["m_juveniles"]!!.toInt(),
                map["m_s4ad_perif"]!!.toInt(),
                map["m_s4ad_cerca"]!!.toInt(),
                map["m_s4ad_lejos"]!!.toInt(),
                map["m_otros_sams_perif"]!!.toInt(),
                map["m_otros_sams_cerca"]!!.toInt(),
                map["m_otros_sams_lejos"]!!.toInt(),
                map["unidsocial_fecha"]!!,
                map["unidsocial_latitud"]!!.toDouble(),
                map["unidsocial_longitud"]!!.toDouble(),
                map["photo_path"]!!,
                map["comentario"]!!
            )
            listaEntidades.add(entidadPlanta)
        }
        return listaEntidades
    }
}