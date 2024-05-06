package com.example.demo.compartir.importar

import android.content.Context
import android.util.Log
import com.example.demo.compartir.exportar.Compartible
import com.example.demo.compartir.exportar.NsdHelper

class ImportarWF(context: Context, private val callback: RegistroDistribuible
): Compartible {

    private lateinit var mtuClienteWF: MTUClienteWF
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

    fun activarComoMTU() {
        mtuClienteWF = MTUClienteWF(callback)
        mtuClienteWF.startListening()
    }
}
