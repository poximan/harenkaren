package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import phocidae.mirounga.leonina.model.EntidadesPlanas
import java.util.UUID

class DemapKarenCSV(context: Context) : Desmapeable {

    override fun desmapear(entidades: List<Map<String, String>>): List<EntidadesPlanas> {
        val listaEntidades = mutableListOf<EntidadesPlanas>()

        for (map in entidades) {

            val entidadPlanta = EntidadesPlanas(
                map["celular_id"]!!,
                UUID.fromString(map["dia_id"]),
                map["dia_orden"]!!.toInt(),
                map["dia_fecha"]!!,
                UUID.fromString(map["recorr_id"]),
                UUID.fromString(map["dia_id"]),
                map["recorr_orden"]!!.toInt(),
                map["observador"]!!,
                map["recorr_fecha_ini"]!!,
                map["recorr_fecha_fin"]!!,
                map["recorr_latitud_ini"]!!.toDouble(),
                map["recorr_longitud_ini"]!!.toDouble(),
                map["recorr_latitud_fin"]!!.toDouble(),
                map["recorr_longitud_fin"]!!.toDouble(),
                map["area_recorrida"]!!,
                map["meteo"]!!,
                map["marea"]!!,
                map["observaciones"]!!,
                UUID.fromString(map["unsoc_id"]),
                UUID.fromString(map["recorr_id"]),
                map["unsoc_orden"]!!.toInt(),
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
                map["unsoc_fecha"]!!,
                map["unsoc_latitud"]!!.toDouble(),
                map["unsoc_longitud"]!!.toDouble(),
                map["photo_path"]!!,
                map["comentario"]!!
            )
            listaEntidades.add(entidadPlanta)
        }
        return listaEntidades
    }
}