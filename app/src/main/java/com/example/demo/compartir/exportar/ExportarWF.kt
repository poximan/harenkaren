package com.example.demo.compartir.exportar

import android.content.Context
import android.os.Parcelable
import android.util.Log
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
        nsdHelper.tearDown()
    }

    fun activarComoRTU(lista: ArrayList<Parcelable>, ipMTU: String) {
        rtuServWF = RTUServWF(ipMTU)
        rtuServWF.sendData(lista)
    }

    fun levantarModal(){
        nsdHelper.showServiceListDialog()
    }
}