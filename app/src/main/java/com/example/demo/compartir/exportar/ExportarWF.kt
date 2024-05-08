package com.example.demo.compartir.exportar

import android.content.Context
import android.os.Parcelable
import android.util.Log
import com.example.demo.compartir.NsdHelper

class ExportarWF(context: Context): ServiceListDialog.ServiceSelectedListener {

    private lateinit var rtuServWF: RTUServWF
    private val nsdHelper: NsdHelper = NsdHelper(context)

    private lateinit var listaParcel: ArrayList<Parcelable>
    private var port = 0

    fun descubrir(lista: ArrayList<Parcelable>) {
        this.listaParcel = lista
        nsdHelper.apply {
            if(port == 0){
                port = initializeServerSocket()
                registerService(port)
            }
            discoverServices()
        }
    }

    fun desconectar() {
        port = 0
        nsdHelper.tearDown()
    }

    private fun activarComoRTU() {
        val ip = "192"
        rtuServWF = RTUServWF(ip, port)
        rtuServWF.sendData(listaParcel)
    }

    fun levantarModal(){
        nsdHelper.showServiceListDialog(this)
    }

    override fun onServiceSelected(serviceName: String) {
        Log.i(NsdHelper.TAG, "llego hasta aca: $serviceName")
        val (ip, puerto) = obtenerDatos(serviceName) ?: run {
            println("No se encontró dirección IP y puerto en el texto.")
            return
        }
        println("Dirección IP: $ip")
        println("Puerto: $puerto")
    }

    private fun obtenerDatos(texto: String): Pair<String, Int>? {
        val regex = Regex("""\b(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}):(\d+)\b""")
        val matchResult = regex.find(texto)
        return matchResult?.let {
            val ip = it.groupValues[1]
            val puerto = it.groupValues[2].toInt()
            ip to puerto
        }
    }
}