package com.example.demo.compartir.importar

import com.example.demo.model.EntidadesPlanas

interface Desmapeable {
    fun desmapear(entidades: List<Map<String, String>>): List<EntidadesPlanas>
}