package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

class MTUClienteBT(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun startListeningForRTUConnection(callback: GestorBT.MessageReceivedCallback) {
        val thread = Thread {
            try {
                val serverSocket: BluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    "BluetoothMTU", GestorBT.BLUETOOTH_UUID
                )

                var receivedMessage = ""
                Log.i("MTU", "escuchando stream entrada")
                val socket: BluetoothSocket = serverSocket.accept()

                if (socket.isConnected) {
                    val inputStream = socket.inputStream
                    val buffer = ByteArray(1024)
                    val bytes = inputStream.read(buffer)
                    receivedMessage = String(buffer, 0, bytes)
                } else {
                    Log.e("MTU", "socket desconectado")
                }

                callback.onMessageReceived(receivedMessage)
                serverSocket.close()

            } catch (e: IOException) {
                Log.d("falla", e.stackTraceToString())
            } catch (e: Exception) {
                Log.d("falla general", e.stackTraceToString())
            }
        }
        thread.start()
    }
}
