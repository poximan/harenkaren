package com.example.demo.compartir.importar

import android.content.Context
import android.util.Log
import com.example.demo.compartir.Compartible
import com.example.demo.compartir.NsdHelper

class ImportarWF(context: Context, private val callback: RegistroDistribuible
): Compartible {

    private lateinit var mtuClienteWF: MTUClienteWF
    private val nsdHelper: NsdHelper = NsdHelper(context)
    private var port = 0

    override fun descubrir() {
        nsdHelper.apply {
            if(port == 0){
                port = initializeServerSocket()
                Log.i("tiempo","primero")
                registerService(port)
                Log.i("tiempo", "segundo")
            }
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
