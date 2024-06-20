package com.example.demo.fragment.add

import androidx.fragment.app.Fragment
import com.example.demo.model.UnidadSociable
import kotlin.reflect.KFunction2

abstract class SuperAdd : Fragment(), UnidadSociable {

    protected lateinit var colectar: (Int, Map<String, Any>) -> Unit
    protected val map: MutableMap<String, Any> = mutableMapOf()

    fun newInstance(
        colectarFunc: KFunction2<Int, Map<String, Any>, Unit>
    ): UnidadSociable {
        colectar = colectarFunc
        return this
    }
}
