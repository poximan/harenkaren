package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothRTU(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun connectToMTU(mtuMacAddress: String) {
        val mtuDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(mtuMacAddress)
        if (mtuDevice == null) {
            // Manejar error: dispositivo maestro no encontrado
            return
        }

        try {
            // Crear un socket Bluetooth para la comunicación
            val socket: BluetoothSocket = mtuDevice.createRfcommSocketToServiceRecord(BluetoothManager.BLUETOOTH_UUID)

            // Conectar al dispositivo maestro
            socket.connect()

            // Enviar el mensaje al dispositivo maestro
            val outputStream = socket.outputStream
            val message = "test1"
            outputStream.write(message.toByteArray())

            Log.i("saliendo", "se saco dato desde RTU")
            // Cerrar el socket después de enviar el mensaje
            socket.close()
        } catch (e: IOException) {
            // Manejar error de conexión
        }
    }
}