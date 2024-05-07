package com.example.demo.compartir.exportar

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import android.widget.Toast
import com.example.demo.compartir.Compartible
import java.util.UUID

class ExportarBT(
    private val context: Context,
    private val masterBT: String
): Compartible {

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
                Toast.makeText(context, "No se conoce ID", Toast.LENGTH_LONG).show()
        }
        return null
    }

    @SuppressLint("MissingPermission")
    fun obtenerDispositivosEmparejados(): Set<BluetoothDevice> {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: return emptySet()

        return bluetoothAdapter.bondedDevices
    }

    override fun desconectar() {
    }

    override fun descubrir() {
    }
}