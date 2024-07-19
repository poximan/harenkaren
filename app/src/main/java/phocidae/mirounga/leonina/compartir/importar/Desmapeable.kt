package phocidae.mirounga.leonina.compartir.importar

import phocidae.mirounga.leonina.model.EntidadesPlanas

interface Desmapeable {
    fun desmapear(entidades: List<Map<String, String>>): List<EntidadesPlanas>
}