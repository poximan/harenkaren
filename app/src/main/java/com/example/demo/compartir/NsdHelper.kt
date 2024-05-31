package com.example.demo.compartir

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.demo.compartir.exportar.ExportarWF
import com.example.demo.compartir.exportar.ServiceListDialog
import com.example.demo.servicios.GestorUUID
import java.net.ServerSocket

class NsdHelper(private val context: Context) {

    companion object {
        const val TAG = "descubrir"
        private const val SERV_NAME = "censos"
        private const val SERV_TYPE = "_http._tcp."
    }

    private lateinit var nsdManager: NsdManager
    private lateinit var serverSocket: ServerSocket
    private val mServiceName: String = "$SERV_NAME@${androidID()}"

    val mapNSD: MutableMap<Int, NsdServiceInfo> = mutableMapOf()
    private var descubrirActivado = false

    /*
    ==========================================
    ANUNCIAR UN SERVICIO
    ==========================================
     */

    fun initializeServerSocket(): Int {

        if (!::serverSocket.isInitialized || serverSocket.isClosed) {
            Log.i(TAG, "anunciando mi servicio")
            // Initialize a server socket on the next available port.
            serverSocket = ServerSocket(0).also { socket ->
                Log.i(TAG, "puerto asig es ${socket.localPort}")
            }
        } else
            Log.i(TAG, "mi servicio fue anunciado previamente")
        return serverSocket.localPort
    }

    fun registerService(mLocalPort: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = mServiceName
            serviceType = SERV_TYPE
            port = mLocalPort

            setAttribute("origen", androidID())
        }
        nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }
    }

    fun miNombre(): String {
        return mServiceName
    }

    // --------------- CLASE ANONIMA
    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Registration failed! Put debugging code here to determine why.
        }

        override fun onServiceUnregistered(arg0: NsdServiceInfo) {
            // Service has been unregistered. This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Unregistration failed. Put debugging code here to determine why.
        }
    }

    /*
    ==========================================
    DESCUBRIR UN SERVICIO
    ==========================================
     */

    fun discoverServices() {
        if (!descubrirActivado) {
            descubrirActivado = true

            Log.i(TAG, "activando descubridor de servicios")
            nsdManager.discoverServices(
                SERV_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )
        } else
            Log.i(TAG, "el descubridor fue activado previamente")
    }

    // --------------- CLASE ANONIMA
    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            Log.i(TAG, "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.i(TAG, "Service discovery success $service")
            when {
                service.serviceType != SERV_TYPE || !service.serviceName.contains(SERV_NAME) ->
                    Log.e(
                        TAG,
                        "Unknown Service Type: ${service.serviceName} - ${service.serviceType}"
                    )

                /*
                la aparente comparacion redundante primero "==" y luego "contains" es porque si dos dispositivos en la red
                tienen instalada la misma app (como es este el caso), ella vera dos instancias; a si misma y a la semejante
                en el otro dispositivo, cuyo nombre tendra el formato "$SERV_NAME (n)"
                 */
                service.serviceName == mServiceName ->
                    Log.i(TAG, "este: ${service.serviceName}")

                service.serviceName != mServiceName -> {
                    Log.i(TAG, "otro: ${service.serviceName}")
                    nsdManager.resolveService(service, resolveListener)
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")

            val clave = mapNSD.entries.find { it.value.serviceName == service.serviceName }?.key
            if (clave != null)
                mapNSD.remove(clave)
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    fun showServiceListDialog(claseQueLeInteresaRetorno: ExportarWF) {

        val serviceListDialog = ServiceListDialog(context, mapNSD)
        serviceListDialog.setServiceSelectedListener(claseQueLeInteresaRetorno)

        val listNsdServiceInfo = mutableListOf<String>()
        for ((clave, valor) in mapNSD) {
            val texto = presentarAmigable(valor)
            listNsdServiceInfo.add("$clave- $texto")
        }
        serviceListDialog.updateServices(listNsdServiceInfo)
        serviceListDialog.show()
    }

    // --------------- CLASE ANONIMA
    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {

            if (serviceInfo.serviceName != SERV_NAME) {

                val texto = presentarAmigable(serviceInfo)
                Log.i(TAG, "otro (desglose): $texto")

                val posicion = mapNSD.size + 1
                mapNSD[posicion] = serviceInfo
                return
            }
        }
    }

    /*
   ==========================================
   EL QUE ANUNCIA && DESCUBRE SERVICIOS
   ==========================================
    */

    fun tearDown() {
        try {
            nsdManager.apply {
                unregisterService(registrationListener)
                stopServiceDiscovery(discoveryListener)
            }
            serverSocket.close()

            descubrirActivado = false
            mapNSD.clear()
        } catch (e: IllegalArgumentException) {
        }
    }

    private fun androidID(): String {
        return GestorUUID.obtenerAndroidID().substringAfter("@")
    }

    private fun presentarAmigable(serviceInfo: NsdServiceInfo): String {
        val host = serviceInfo.host.hostAddress
        val port = serviceInfo.port
        return "${serviceInfo.serviceName}\n$host:$port"
    }

    fun getSocket(): ServerSocket {
        return serverSocket
    }
}
