package phocidae.mirounga.leonina.compartir.importar

interface ListaImportable {
    fun onPelosReceived(message: ArrayList<Map<String, String>>)
    fun progreso(valor: Float)
}