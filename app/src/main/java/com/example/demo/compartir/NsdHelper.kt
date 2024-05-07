package com.example.demo.compartir

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.demo.activity.MainActivity
import com.example.demo.compartir.exportar.ServiceListDialog
import java.net.ServerSocket

class NsdHelper(private val context: Context) {

    private val listNsdServiceInfo = mutableListOf<String>()

    companion object {
        const val TAG = "descubrir"
        private const val SERV_NAME = "compartirCensos"
        private const val SERV_TYPE = "_http._tcp."
    }

    private lateinit var nsdManager: NsdManager
    private var mServiceName: String = SERV_NAME

    private lateinit var serverSocket: ServerSocket

    private var descubrirActivado = false
    private var resolverActivado = false

    /*
    ==========================================
    ANUNCIAR UN SERVICIO
    ==========================================
     */

    fun initializeServerSocket(): Int {

        if(!::serverSocket.isInitialized){
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
            setAttribute("origen", MainActivity.obtenerAndroidID())
        }

        nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }
    }

    // --------------- CLASE ANONIMA
    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            mServiceName = nsdServiceInfo.serviceName
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
                service.serviceType != SERV_TYPE ->
                    Log.e(TAG, "Unknown Service Type: ${service.serviceType}")

                /*
                la aparente comparacion redundante primero "==" y luego "contains" es porque si dos dispositivos en la red
                tienen instalada la misma app (como es este el caso), ella vera dos instancias; a si misma y a la semejante
                en el otro dispositivo, cuyo nombre tendra el formato "$SERV_NAME (1)"
                 */
                service.serviceName == SERV_NAME ->
                    Log.i(TAG, "este: $SERV_NAME")

                service.serviceName.contains(SERV_NAME) -> {
                    Log.i(TAG, "otro: ${service.serviceName}")
                    if (!resolverActivado) {
                        resolverActivado = true
                        nsdManager.resolveService(service, resolveListener)
                    }
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")
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

    // --------------- CLASE ANONIMA
    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {

            if (serviceInfo.serviceName != SERV_NAME) {

                val byteArray = serviceInfo.attributes["origen"]
                val origen: String = String(byteArray!!).substringAfter("@")
                val host = serviceInfo.host.hostAddress
                val port = serviceInfo.port
                val posicion = listNsdServiceInfo.size+1
                val texto = "$origen -> ${serviceInfo.serviceName} $host:$port"

                Log.i(TAG, "otro (desglose): $texto")
                listNsdServiceInfo.add("$posicion- $texto")
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
        nsdManager.apply {
            try {
                unregisterService(registrationListener)
                stopServiceDiscovery(discoveryListener)

                descubrirActivado = false
                resolverActivado = false
            } catch (e: IllegalArgumentException){}
        }
    }

    fun showServiceListDialog() {
        val serviceListDialog = ServiceListDialog(context)
        serviceListDialog.updateServices(listNsdServiceInfo)
        serviceListDialog.show()
    }
}