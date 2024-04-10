package com.example.demo.export

import android.os.Parcelable

class GestorWF(private val callback: RegistroDistribuible) {

    private var mtuClienteWF: MTUClienteWF? = null
    private var rtuServWF: RTUServWF? = null

    fun activarComoMTU() {
        mtuClienteWF = MTUClienteWF(callback)
        mtuClienteWF?.startListening()
    }

    fun activarComoRTU(lista: ArrayList<Parcelable>, ipMTU: String) {

        rtuServWF = RTUServWF(ipMTU)
        rtuServWF?.sendData(lista)
    }
}
