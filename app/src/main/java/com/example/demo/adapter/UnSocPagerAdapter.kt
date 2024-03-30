package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.fragment.add.UnSocVivosFragment
import com.example.demo.fragment.add.UnSocMuertosFragment
import com.example.demo.model.UnidSocial

class UnSocPagerAdapter(fm: FragmentManager, private val unidSocial: UnidSocial) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // Lista de títulos para cada pestaña
    private val tabTitles = arrayOf("General", "Indiv. vivos", "Indiv. muertos")

    override fun getItem(position: Int): Fragment {
        // Retorna un nuevo fragmento para cada posición
        return when (position) {
            0 -> UnSocGralFragment(unidSocial)
            1 -> UnSocVivosFragment(unidSocial)
            2 -> UnSocMuertosFragment(unidSocial)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getCount(): Int {
        // Retorna el número total de pestañas
        return tabTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // Retorna el título de la pestaña en la posición dada
        return tabTitles[position]
    }
}