package com.example.demo.export

class ImportarWF(private val callback: RegistroDistribuible) {

    private var mtuClienteWF: MTUClienteWF? = null

    fun activarComoMTU() {
        mtuClienteWF = MTUClienteWF(callback)
        mtuClienteWF?.startListening()
    }
}
