package phocidae.mirounga.leonina.adapter

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.exception.FaltaLatLongExcepcion
import phocidae.mirounga.leonina.exception.FaltaVivosExcepcion
import phocidae.mirounga.leonina.fragment.add.UnSocAddGralFragment
import phocidae.mirounga.leonina.fragment.add.UnSocAddMuertosFragment
import phocidae.mirounga.leonina.fragment.add.UnSocAddVivosFragment
import phocidae.mirounga.leonina.fragment.edit.UnSocEditGralFragment
import phocidae.mirounga.leonina.fragment.edit.UnSocEditMuertosFragment
import phocidae.mirounga.leonina.fragment.edit.UnSocEditVivosFragment
import phocidae.mirounga.leonina.model.UnidSocial
import java.util.Calendar

class UnSocPagerAdapter(
    fm: FragmentManager,
    private val context: Context,
    unSoc: UnidSocial? = null
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val solapas = if (unSoc == null) {
        // nueva instancia
        arrayOf(
            UnSocAddGralFragment().newInstance(::colectar),
            UnSocAddVivosFragment().newInstance(::colectar),
            UnSocAddMuertosFragment().newInstance(::colectar)
        )
    } else {
        // editar
        arrayOf(
            UnSocEditGralFragment().editInstance(::colectar, unSoc),
            UnSocEditVivosFragment().editInstance(::colectar, unSoc),
            UnSocEditMuertosFragment().editInstance(::colectar, unSoc)
        )
    }

    private var map: MutableMap<String, Any> = mutableMapOf(
        "pto_observacion" to (unSoc?.ptoObsUnSoc ?: ""),
        "ctx_social" to (unSoc?.ctxSocial ?: ""),
        "tpo_sustrato" to (unSoc?.tpoSustrato ?: ""),
        "v_alfa_s4ad" to (unSoc?.vAlfaS4Ad ?: 0),
        "v_alfa_sams" to (unSoc?.vAlfaSams ?: 0),
        "v_hembras_ad" to (unSoc?.vHembrasAd ?: 0),
        "v_crias" to (unSoc?.vCrias ?: 0),
        "v_destetados" to (unSoc?.vDestetados ?: 0),
        "v_juveniles" to (unSoc?.vJuveniles ?: 0),
        "v_s4ad_perif" to (unSoc?.vS4AdPerif ?: 0),
        "v_s4ad_cerca" to (unSoc?.vS4AdCerca ?: 0),
        "v_s4ad_lejos" to (unSoc?.vS4AdLejos ?: 0),
        "v_otros_sams_perif" to (unSoc?.vOtrosSamsPerif ?: 0),
        "v_otros_sams_cerca" to (unSoc?.vOtrosSamsCerca ?: 0),
        "v_otros_sams_lejos" to (unSoc?.vOtrosSamsLejos ?: 0),
        "m_alfa_s4ad" to (unSoc?.mAlfaS4Ad ?: 0),
        "m_alfa_sams" to (unSoc?.mAlfaSams ?: 0),
        "m_hembras_ad" to (unSoc?.mHembrasAd ?: 0),
        "m_crias" to (unSoc?.mCrias ?: 0),
        "m_destetados" to (unSoc?.mDestetados ?: 0),
        "m_juveniles" to (unSoc?.mJuveniles ?: 0),
        "m_s4ad_perif" to (unSoc?.mS4AdPerif ?: 0),
        "m_s4ad_cerca" to (unSoc?.mS4AdCerca ?: 0),
        "m_s4ad_lejos" to (unSoc?.mS4AdLejos ?: 0),
        "m_otros_sams_perif" to (unSoc?.mOtrosSamsPerif ?: 0),
        "m_otros_sams_cerca" to (unSoc?.mOtrosSamsCerca ?: 0),
        "m_otros_sams_lejos" to (unSoc?.mOtrosSamsLejos ?: 0),
        "date" to (unSoc?.date ?: ""),
        "latitud" to (unSoc?.latitud ?: 0.0),
        "longitud" to (unSoc?.longitud ?: 0.0),
        "comentario" to (unSoc?.comentario ?: "")
    )

    private fun colectar(position: Int, mapaActual: Map<String, Any>) {

        val instante = String.format("%02d", Calendar.getInstance().get(Calendar.SECOND))
        Log.i("pagerAdapter", "${instante}: carga de datos desde solapa ${getPageTitle(position)}")

        when (position) {
            0 -> {
                map["pto_observacion"] = mapaActual["pto_observacion"] as String
                map["ctx_social"] = mapaActual["ctx_social"] as String
                map["tpo_sustrato"] = mapaActual["tpo_sustrato"] as String
                map["latitud"] = mapaActual["latitud"] as Double
                map["longitud"] = mapaActual["longitud"] as Double
                map["comentario"] = mapaActual["comentario"] as String
            }

            1 -> {
                map["v_alfa_s4ad"] = mapaActual["v_alfa_s4ad"] as Int
                map["v_alfa_sams"] = mapaActual["v_alfa_sams"] as Int
                map["v_hembras_ad"] = mapaActual["v_hembras_ad"] as Int
                map["v_crias"] = mapaActual["v_crias"] as Int
                map["v_destetados"] = mapaActual["v_destetados"] as Int
                map["v_juveniles"] = mapaActual["v_juveniles"] as Int
                map["v_s4ad_perif"] = mapaActual["v_s4ad_perif"] as Int
                map["v_s4ad_cerca"] = mapaActual["v_s4ad_cerca"] as Int
                map["v_s4ad_lejos"] = mapaActual["v_s4ad_lejos"] as Int
                map["v_otros_sams_perif"] = mapaActual["v_otros_sams_perif"] as Int
                map["v_otros_sams_cerca"] = mapaActual["v_otros_sams_cerca"] as Int
                map["v_otros_sams_lejos"] = mapaActual["v_otros_sams_lejos"] as Int
            }

            2 -> {
                map["m_alfa_s4ad"] = mapaActual["m_alfa_s4ad"] as Int
                map["m_alfa_sams"] = mapaActual["m_alfa_sams"] as Int
                map["m_hembras_ad"] = mapaActual["m_hembras_ad"] as Int
                map["m_crias"] = mapaActual["m_crias"] as Int
                map["m_destetados"] = mapaActual["m_destetados"] as Int
                map["m_juveniles"] = mapaActual["m_juveniles"] as Int
                map["m_s4ad_perif"] = mapaActual["m_s4ad_perif"] as Int
                map["m_s4ad_cerca"] = mapaActual["m_s4ad_cerca"] as Int
                map["m_s4ad_lejos"] = mapaActual["m_s4ad_lejos"] as Int
                map["m_otros_sams_perif"] = mapaActual["m_otros_sams_perif"] as Int
                map["m_otros_sams_cerca"] = mapaActual["m_otros_sams_cerca"] as Int
                map["m_otros_sams_lejos"] = mapaActual["m_otros_sams_lejos"] as Int
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return solapas[position] as Fragment
    }

    override fun getCount(): Int {
        return solapas.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (solapas[position]) {
            is UnSocAddGralFragment -> context.getString(R.string.adap_gralToString)
            is UnSocEditGralFragment -> context.getString(R.string.adap_gralToString)
            is UnSocAddVivosFragment -> context.getString(R.string.adap_vivosToString)
            is UnSocEditVivosFragment -> context.getString(R.string.adap_vivosToString)
            is UnSocAddMuertosFragment -> context.getString(R.string.adap_muertosToString)
            is UnSocEditMuertosFragment -> context.getString(R.string.adap_muertosToString)
            else -> {
                "error TAG!"
            }
        }
    }

    fun transferirDatos(): MutableMap<String, Any> {
        if (map["latitud"] == 0.0 || map["longitud"] == 0.0)
            throw FaltaLatLongExcepcion(context.getString(R.string.varias_validarGPS))

        if (
            map["v_alfa_s4ad"] == 0 && map["v_alfa_sams"] == 0 && map["v_hembras_ad"] == 0 &&
            map["v_crias"] == 0 && map["v_destetados"] == 0 && map["v_juveniles"] == 0 &&
            map["v_s4ad_perif"] == 0 && map["v_s4ad_cerca"] == 0 && map["v_s4ad_lejos"] == 0 &&
            map["v_otros_sams_perif"] == 0 && map["v_otros_sams_cerca"] == 0 && map["v_otros_sams_lejos"] == 0
        )
            throw FaltaVivosExcepcion(context.getString(R.string.adap_validarVivos))

        return map
    }
}