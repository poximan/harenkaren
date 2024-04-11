package com.example.demo.export

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager

class ImportarBT(
    private val context: Context,
    private val callback: RegistroDistribuible
) {

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
}
