package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.lang.Integer.min

class RTUServBT(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun connectToMTU(mtuMacAddress: String) {
        val mtuDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(mtuMacAddress)
        if (mtuDevice == null) {
            // Manejar error: dispositivo maestro no encontrado
            return
        }

        try {
            // Crear un socket Bluetooth para la comunicación
            val socket: BluetoothSocket = mtuDevice.createRfcommSocketToServiceRecord(GestorBT.BLUETOOTH_UUID)

            // Conectar al dispositivo maestro
            socket.connect()

            // Enviar el mensaje al dispositivo maestro
            val outputStream = socket.outputStream
            val message = "test1"
            val bufferSize = 1024 // Tamaño del búfer en bytes

            // Convertir el mensaje a bytes
            val messageBytes = message.toByteArray()

            // Calcular el número de paquetes necesarios
            val numPackets = (messageBytes.size + bufferSize - 1) / bufferSize

            // Escribir cada paquete en el OutputStream
            var offset = 0
            for (i in 0 until numPackets) {
                val packetSize = min(bufferSize, messageBytes.size - offset)
                outputStream.write(messageBytes, offset, packetSize)
                offset += packetSize
            }

            // Flushear y cerrar el OutputStream después de enviar todos los datos
            outputStream.flush()
            outputStream.close()

        } catch (e: IOException) {
            // Manejar error de conexión
        }
    }
}