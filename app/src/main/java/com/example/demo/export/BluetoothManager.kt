package com.example.demo.export

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class BluetoothManager(private val masterBT: String, private val activity: Activity, private val context: Context) {

    constructor(activity: Activity, context: Context) : this("", activity, context)

    companion object {
        val BLUETOOTH_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        const val BLUETOOTH_CONNECT_PERMISSION_CODE = 1
    }

    fun activarComoMTU() {
        // Crear una instancia de BluetoothMTU y comenzar a escuchar para recibir datos
        val mtu = obtenerBluetoothAdapter()?.let { BluetoothMTU(it) }
        mtu?.startListeningForRTUConnection() { }
    }

    fun activarComoRTU() {

        var adapter = obtenerBluetoothAdapter() ?.let { it }

        val direccionMAC = adapter?.let { obtenerDireccionMAC(it, masterBT) }

        val rtu = adapter?.let { BluetoothRTU(it) }
        if (direccionMAC != null) {
            rtu?.connectToMTU(direccionMAC)
        }
    }

    private fun obtenerBluetoothAdapter(): BluetoothAdapter? {
        // Verificar si el dispositivo soporta Bluetooth
        if (!context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_BLUETOOTH)) {
            // El dispositivo no soporta Bluetooth
            return null
        }

        // Retornar la instancia de BluetoothAdapter
        return BluetoothAdapter.getDefaultAdapter()
            ?: // El dispositivo no tiene Bluetooth
            return null
    }

    @SuppressLint("MissingPermission")
    private fun obtenerDireccionMAC(bluetoothAdapter: BluetoothAdapter, nombreDispositivo: String): String? {
        // Verificar si el adaptador Bluetooth está habilitado
        if (!bluetoothAdapter.isEnabled) {
            return null
        }

        // Obtener la lista de dispositivos emparejados
        val dispositivosEmparejados = obtenerDispositivosEmparejados()

        for (dispositivo in dispositivosEmparejados) {
            if (dispositivo.name == nombreDispositivo) {
                // Se encontró el dispositivo, retornar su dirección MAC
                return dispositivo.address
            }
        }
        return null
    }


    private fun obtenerDispositivosEmparejados(): Set<BluetoothDevice>{

        // El dispositivo no admite Bluetooth
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?:
            return emptySet()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes el permiso, solicítalo
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                BLUETOOTH_CONNECT_PERMISSION_CODE
            )
            return emptySet()
        }

        // Si ya tienes el permiso, accede a la lista de dispositivos emparejados
        return bluetoothAdapter.bondedDevices
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == BLUETOOTH_CONNECT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso BLUETOOTH_CONNECT ha sido concedido, accede a la lista de dispositivos emparejados
                obtenerDispositivosEmparejados()
            } else {
                // El permiso BLUETOOTH_CONNECT ha sido denegado, maneja esta situación
            }
        }
    }
}
