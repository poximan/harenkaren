package com.example.demo.compartir.exportar

import android.content.Context
import android.os.Parcelable
import com.example.demo.compartir.Compartible
import com.example.demo.compartir.NsdHelper

class ExportarWF(context: Context): Compartible {

    private lateinit var rtuServWF: RTUServWF
    private val nsdHelper: NsdHelper = NsdHelper(context)
    private var port = 0

    override fun descubrir() {
        nsdHelper.apply {
            if(port == 0){
                port = initializeServerSocket()
                registerService(port)
            }
            discoverServices()
        }
    }

    override fun desconectar() {
        port = 0
        nsdHelper.tearDown()
    }

    fun activarComoRTU(lista: ArrayList<Parcelable>) {
        val ip = "192"
        rtuServWF = RTUServWF(ip, port)
        rtuServWF.sendData(lista)
    }

    fun levantarModal(){
        nsdHelper.showServiceListDialog()
    }
}