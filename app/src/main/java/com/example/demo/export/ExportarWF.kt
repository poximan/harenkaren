package com.example.demo.export

import android.os.Parcelable

class ExportarWF() {

    private var rtuServWF: RTUServWF? = null

    fun activarComoRTU(lista: ArrayList<Parcelable>, ipMTU: String) {

        rtuServWF = RTUServWF(ipMTU)
        rtuServWF?.sendData(lista)
    }
}
