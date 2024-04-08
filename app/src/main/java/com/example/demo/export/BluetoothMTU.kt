package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.io.IOException

class BluetoothMTU(private val bluetoothAdapter: BluetoothAdapter) {

    interface MessageReceivedCallback {
        fun onMessageReceived(message: String)
    }

    @SuppressLint("MissingPermission")
    fun startListeningForRTUConnection(callback: MessageReceivedCallback) {
        // Crear un hilo para manejar la conexión en segundo plano
        val thread = Thread {
            try {
                // Crear un socket de servidor Bluetooth
                val serverSocket: BluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    "BluetoothMTU", BluetoothManager.BLUETOOTH_UUID
                )

                // Esperar a que la RTU se conecte
                val socket: BluetoothSocket = serverSocket.accept()

                // Leer el mensaje recibido de la RTU
                val inputStream = socket.inputStream
                val buffer = ByteArray(1024)
                val bytes = inputStream.read(buffer)
                val receivedMessage = String(buffer, 0, bytes)

                // Llamar al callback con el mensaje recibido
                callback.onMessageReceived(receivedMessage)

                serverSocket.close()
            } catch (e: IOException) { }
        }
        thread.start()
    }
}

