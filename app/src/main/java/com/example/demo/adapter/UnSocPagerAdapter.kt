package com.example.demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.demo.fragment.add.Solapa1Fragment
import com.example.demo.fragment.add.Solapa2Fragment
import com.example.demo.fragment.add.Solapa3Fragment

class UnSocPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // Lista de títulos para cada pestaña
    private val tabTitles = arrayOf("General", "Indiv. vivos", "Indiv. muertos")

    override fun getItem(position: Int): Fragment {
        // Retorna un nuevo fragmento para cada posición
        return when (position) {
            0 -> Solapa1Fragment()
            1 -> Solapa2Fragment()
            2 -> Solapa3Fragment()
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