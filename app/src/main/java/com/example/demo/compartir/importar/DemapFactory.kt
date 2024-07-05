package com.example.demo.compartir.importar

import android.content.Context
import com.example.demo.exception.DemaperExcepcion

object DemapFactory {
    fun crearDemap(context: Context, header: Map<String, String>): Desmapeable {
        return when (header.size) {
            51 -> DemapKarenCSV(context)
            46 -> DemapKarenLegadoCSV(context)
            11 -> DemapPelosCSV(context)
            else -> throw DemaperExcepcion("formato csv desconocido")
        }
    }
}