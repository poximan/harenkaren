package com.example.demo.compartir.importar

interface ListaImportable {
    fun onPelosReceived(message: ArrayList<Map<String, String>>)
    fun progreso(valor: Float)
}