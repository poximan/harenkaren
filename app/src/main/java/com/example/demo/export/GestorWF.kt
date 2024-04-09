package com.example.demo.export

class GestorWF {

    private var mtuClienteWF: MTUClienteWF? = null
    private var rtuServWF: RTUServWF? = null

    fun activarComoMTU() {
        mtuClienteWF = MTUClienteWF { data ->
            // Aquí puedes manejar los datos recibidos desde el cliente
            println("Datos recibidos como MTU: $data")
        }
        mtuClienteWF?.startListening()
    }

    fun activarComoRTU(ipMTU: String) {

        rtuServWF = RTUServWF(ipMTU)
        rtuServWF?.sendData("test")
    }
}
