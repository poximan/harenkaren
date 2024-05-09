package com.example.demo.compartir.exportar

import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.os.Parcelable
import android.util.Log
import com.example.demo.compartir.NsdHelper

class ExportarWF(context: Context) : ServiceListDialog.ServiceSelectedListener {

    private lateinit var rtuServWF: RTUServWF
    private val nsdHelper: NsdHelper = NsdHelper(context)

    private lateinit var listaParcel: ArrayList<Parcelable>
    private var port = 0

    fun descubrir(lista: ArrayList<Parcelable>) {
        this.listaParcel = lista
        nsdHelper.apply {
            if (port == 0) {
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

    private fun activarComoRTU(ip: String, port: Int) {
        rtuServWF = RTUServWF(ip, port)
        rtuServWF.sendData(listaParcel)
    }

    fun levantarModal() {
        nsdHelper.showServiceListDialog(this)
    }

    override fun onServiceSelected(serviceInfo: NsdServiceInfo) {
        Log.i(NsdHelper.TAG, "Dirección IP: ${serviceInfo.host.hostAddress}")
        Log.i(NsdHelper.TAG, "Puerto: ${serviceInfo.port}")
        activarComoRTU(serviceInfo.host.hostAddress, serviceInfo.port)
    }
}