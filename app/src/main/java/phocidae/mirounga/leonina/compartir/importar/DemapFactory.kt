package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import phocidae.mirounga.leonina.exception.DemaperExcepcion

object DemapFactory {
    fun crearDemap(context: Context, header: Map<String, String>): Desmapeable {
        return when (header.size) {
            51 -> DemapKarenCSV()
            46 -> DemapKarenLegadoCSV(context)
            11 -> DemapPelosCSV(context)
            else -> throw DemaperExcepcion("formato csv desconocido")
        }
    }
}