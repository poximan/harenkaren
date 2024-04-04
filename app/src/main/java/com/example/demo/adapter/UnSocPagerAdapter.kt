package com.example.demo.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.fragment.add.UnSocVivosFragment
import com.example.demo.fragment.add.UnSocMuertosFragment
import java.util.Calendar

class UnSocPagerAdapter(
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val solapas = arrayOf(
        UnSocGralFragment().newInstance(::colectar),
        UnSocVivosFragment().newInstance(::colectar),
        UnSocMuertosFragment().newInstance(::colectar))

    private var map: MutableMap<String, Any?> = mutableMapOf(
        "pto_observacion" to "",
        "ctx_social" to "",
        "tpo_sustrato" to "",
        "v_alfa_s4ad" to 0,
        "v_alfa_sams" to 0,
        "v_hembras_ad" to 0,
        "v_crias" to 0,
        "v_destetados" to 0,
        "v_juveniles" to 0,
        "v_s4ad_perif" to 0,
        "v_s4ad_cerca" to 0,
        "v_s4ad_lejos" to 0,
        "v_otros_sams_perif" to 0,
        "v_otros_sams_cerca" to 0,
        "v_otros_sams_lejos" to 0,
        "m_alfa_s4ad" to 0,
        "m_alfa_sams" to 0,
        "m_hembras_ad" to 0,
        "m_crias" to 0,
        "m_destetados" to 0,
        "m_juveniles" to 0,
        "m_s4ad_perif" to 0,
        "m_s4ad_cerca" to 0,
        "m_s4ad_lejos" to 0,
        "m_otros_sams_perif" to 0,
        "m_otros_sams_cerca" to 0,
        "m_otros_sams_lejos" to 0,
        "date" to "",
        "latitud" to 0.0,
        "longitud" to 0.0,
        "photo_path" to "",
        "comentario" to ""
    )


    private fun colectar(position: Int, mapaActual: Map<String, Any>) {

        val instante = String.format("%02d", Calendar.getInstance().get(Calendar.SECOND))
        Log.i("notimap", "${instante}: carga de datos desde solapa ${getPageTitle(position)}")

        when (position) {
            0 -> {
                map["pto_observacion"] = mapaActual["pto_observacion"] as String
                map["ctx_social"] = mapaActual["ctx_social"] as String
                map["tpo_sustrato"] = mapaActual["tpo_sustrato"] as String
                map["latitud"] = mapaActual["latitud"] as Double
                map["longitud"] = mapaActual["longitud"] as Double
                map["photo_path"] = mapaActual["photo_path"] as String
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
        return solapas[position]
    }

    override fun getCount(): Int {
        return solapas.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return solapas[position].toString()
    }

    fun transferirDatos(): MutableMap<String, Any?> {
        return map
    }
}