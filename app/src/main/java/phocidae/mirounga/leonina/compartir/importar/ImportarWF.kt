package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import phocidae.mirounga.leonina.compartir.NsdHelper

class ImportarWF(context: Context, private val callback: RegistroDistribuible) {

    private lateinit var mtuClienteWF: MTUClienteWF
    private val nsdHelper: NsdHelper = NsdHelper(context)
    private var port = 0

    fun descubrir() {
        nsdHelper.apply {
            if (port == 0) {
                port = initializeServerSocket()
                registerService(port)
            }
            discoverServices()
        }
    }

    fun miNombre(): String {
        return nsdHelper.miNombre()
    }

    fun desconectar() {
        port = 0
        nsdHelper.tearDown()
    }

    fun activarComoMTU(): Int {
        mtuClienteWF = MTUClienteWF(callback)
        mtuClienteWF.startListening(nsdHelper.getSocket())
        return port
    }
}
