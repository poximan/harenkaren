package com.example.demo.compartir.importar

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import com.example.demo.compartir.Compartible

class ImportarBT(
    private val context: Context,
    private val callback: RegistroDistribuible
): Compartible {

    fun activarComoMTU() {
        val mtu = obtenerBluetoothAdapter()?.let { MTUClienteBT(it) }
        mtu?.startListeningForRTUConnection(callback)
    }

    private fun obtenerBluetoothAdapter(): BluetoothAdapter? {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            return null
        }

        return BluetoothAdapter.getDefaultAdapter()
            ?: return null
    }

    override fun desconectar() {
    }

    override fun descubrir() {
    }
}
