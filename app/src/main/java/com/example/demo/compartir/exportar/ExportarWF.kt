package com.example.demo.compartir.exportar

import android.content.Context
import android.os.Parcelable
import android.util.Log

class ExportarWF(context: Context): Compartible {

    private lateinit var rtuServWF: RTUServWF
    private val nsdHelper: NsdHelper = NsdHelper(context)

    override fun descubrir() {
        nsdHelper.apply {
            initializeServerSocket()
            Log.i("tiempo", "segundo")
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