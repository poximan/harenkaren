package com.example.demo.export

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class ExportarBT(
    private val masterBT: String,
    private val activity: Activity,
    private val context: Context
) {
    companion object {
        val BLUETOOTH_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        const val PERMISSION_REQUEST_BLUETOOTH = 1
    }

    fun activarComoRTU(lista: ArrayList<Parcelable>) {
        var adapter = obtenerBluetoothAdapter() ?.let { it }

        val direccionMAC = adapter?.let { obtenerDireccionMAC(it, masterBT) }

        val rtu = adapter?.let { RTUServBT(it) }
        if (direccionMAC != null) {
            rtu?.connectToMTU(lista, direccionMAC)
        }
    }

    private fun obtenerBluetoothAdapter(): BluetoothAdapter? {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
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
                PERMISSION_REQUEST_BLUETOOTH
            )
            return emptySet()
        }

        return bluetoothAdapter.bondedDevices
    }
}
