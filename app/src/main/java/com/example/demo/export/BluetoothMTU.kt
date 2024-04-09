package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

class BluetoothMTU(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun startListeningForRTUConnection(callback: BluetoothManager.MessageReceivedCallback) {
        val thread = Thread {
            try {
                val serverSocket: BluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    "BluetoothMTU", BluetoothManager.BLUETOOTH_UUID
                )

                val socket: BluetoothSocket = serverSocket.accept()

                val inputStream = socket.inputStream
                val buffer = ByteArray(1024)
                val bytes = inputStream.read(buffer)
                val receivedMessage = String(buffer, 0, bytes)

                callback.onMessageReceived(receivedMessage)
                Log.i("thread", "recepcionando $receivedMessage")
                serverSocket.close()
            } catch (e: IOException) {
                // Manejar excepciones
            }
        }
        thread.start()
    }
}
