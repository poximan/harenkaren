package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.fragment.add.UnSocVivosFragment
import com.example.demo.fragment.add.UnSocMuertosFragment
import com.example.demo.model.UnidSocial

class UnSocPagerAdapter(
    fm: FragmentManager,
    private val unidSocial: UnidSocial
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(
        UnSocGralFragment().newInstance(::colectar),
        UnSocVivosFragment().newInstance(::colectar),
        UnSocMuertosFragment().newInstance(::colectar))

    private var map: MutableMap<String, Any> = mutableMapOf()

    private fun colectar(position: Int, mapaActual: Map<String, Any>) {
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
                map["v_alfa_otros_sa"] = mapaActual["v_alfa_otros_sa"] as Int
                map["v_hembras_ad"] = mapaActual["v_hembras_ad"] as Int
                map["v_crias"] = mapaActual["v_crias"] as Int
                map["v_destetados"] = mapaActual["v_destetados"] as Int
                map["v_juveniles"] = mapaActual["v_juveniles"] as Int
                map["v_s4ad_perif"] = mapaActual["v_s4ad_perif"] as Int
                map["v_s4ad_cerca"] = mapaActual["v_s4ad_cerca"] as Int
                map["v_s4ad_lejos"] = mapaActual["v_s4ad_lejos"] as Int
                map["v_otros_sa_perif"] = mapaActual["v_otros_sa_perif"] as Int
                map["v_otros_sa_cerca"] = mapaActual["v_otros_sa_cerca"] as Int
                map["v_otros_sa_lejos"] = mapaActual["v_otros_sa_lejos"] as Int
            }
            2 -> {
                map["m_alfa_s4ad"] = mapaActual["m_alfa_s4ad"] as Int
                map["m_alfa_otros_sa"] = mapaActual["m_alfa_otros_sa"] as Int
                map["m_hembras_ad"] = mapaActual["m_hembras_ad"] as Int
                map["m_crias"] = mapaActual["m_crias"] as Int
                map["m_destetados"] = mapaActual["m_destetados"] as Int
                map["m_juveniles"] = mapaActual["m_juveniles"] as Int
                map["m_s4ad_perif"] = mapaActual["m_s4ad_perif"] as Int
                map["m_s4ad_cerca"] = mapaActual["m_s4ad_cerca"] as Int
                map["m_s4ad_lejos"] = mapaActual["m_s4ad_lejos"] as Int
                map["m_otros_sa_perif"] = mapaActual["m_otros_sa_perif"] as Int
                map["m_otros_sa_cerca"] = mapaActual["m_otros_sa_cerca"] as Int
                map["m_otros_sa_lejos"] = mapaActual["m_otros_sa_lejos"] as Int
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position].toString()
    }

    fun transferirDatos(): Map<String, Any> {
        return map
    }
}