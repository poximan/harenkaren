package com.example.demo.export

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class BluetoothManager(
    private val masterBT: String,
    private val activity: Activity,
    private val context: Context,
    private val callback: MessageReceivedCallback
) {

    constructor(activity: Activity, context: Context, callback: MessageReceivedCallback) : this("", activity, context, callback)

    interface MessageReceivedCallback {
        fun onMessageReceived(message: String)
    }

    companion object {
        val BLUETOOTH_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        const val BLUETOOTH_CONNECT_PERMISSION_CODE = 1
    }

    fun activarComoMTU() {
        val mtu = obtenerBluetoothAdapter()?.let { BluetoothMTU(it) }
        mtu?.startListeningForRTUConnection(callback)
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
        if (!context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_BLUETOOTH)) {
            return null
        }

        return BluetoothAdapter.getDefaultAdapter()
            ?: return null
    }

    @SuppressLint("MissingPermission")
    private fun obtenerDireccionMAC(bluetoothAdapter: BluetoothAdapter, nombreDispositivo: String): String? {
        if (!bluetoothAdapter.isEnabled) {
            return null
        }

        val dispositivosEmparejados = obtenerDispositivosEmparejados()

        for (dispositivo in dispositivosEmparejados) {
            if (dispositivo.name == nombreDispositivo)
                return dispositivo.address
            else
                Toast.makeText(activity, "No se conoce ID", Toast.LENGTH_LONG).show()

        }
        return null
    }

    fun obtenerDispositivosEmparejados(): Set<BluetoothDevice> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return emptySet()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                BLUETOOTH_CONNECT_PERMISSION_CODE
            )
            return emptySet()
        }

        return bluetoothAdapter.bondedDevices
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == BLUETOOTH_CONNECT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerDispositivosEmparejados()
            } else {
                // Manejar denegación de permisos
            }
        }
    }
}
