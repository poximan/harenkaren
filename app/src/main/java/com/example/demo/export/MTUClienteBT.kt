package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Parcelable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream

class MTUClienteBT(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun startListeningForRTUConnection(callback: RegistroDistribuible) {
        val thread = Thread {

            var lista: ArrayList<Parcelable>? = null

            try {
                val serverSocket: BluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    "BluetoothMTU", GestorBT.BLUETOOTH_UUID
                )
                println("Esperando conexión...")
                val socket: BluetoothSocket = serverSocket.accept()
                println("Conexión establecida.")

                // Recibir los bytes del socket
                val inputStream = socket.inputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                var byte: Int
                while (inputStream.read().also { byte = it } != -1) {
                    byteArrayOutputStream.write(byte)
                }
                val bytes = byteArrayOutputStream.toByteArray()

                // Convertir los bytes en un ArrayList<Parcelable>
                val byteArrayInputStream = ByteArrayInputStream(bytes)
                val objectInputStream = ObjectInputStream(byteArrayInputStream)
                lista = objectInputStream.readObject() as? ArrayList<Parcelable>

                // Cerrar los streams y el socket
                inputStream.close()
                byteArrayOutputStream.close()
                byteArrayInputStream.close()
                objectInputStream.close()
                socket.close()
                serverSocket.close()

                if (lista != null) {
                    callback.onMessageReceived(lista)
                }
                serverSocket.close()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}
