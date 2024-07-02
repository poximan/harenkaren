package com.example.demo.compartir.importar

import android.os.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.ServerSocket
import java.net.Socket

class MTUClienteWF(private val callback: RegistroDistribuible) {

    companion object {
        private const val TAG = "DesdePar"
    }

    fun startListening(socket: ServerSocket) {
        CoroutineScope(Dispatchers.IO).launch {
            listenForData(socket)
        }
    }

    private suspend fun listenForData(serverSocket: ServerSocket) {
        var socket: Socket? = null
        var inputStream: InputStream? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        var byteArrayInputStream: ByteArrayInputStream? = null
        var objectInputStream: ObjectInputStream? = null

        try {
            socket = withContext(Dispatchers.IO) { serverSocket.accept() }
            inputStream = socket.getInputStream()
            byteArrayOutputStream = ByteArrayOutputStream()
            var byte: Int
            val buffer = ByteArray(1024)
            var bytesRead: Long = 0

            // Leer datos del socket y calcular progreso
            while (withContext(Dispatchers.IO) {
                    inputStream.read(buffer).also { byte = it }
                } != -1) {
                byteArrayOutputStream.write(buffer, 0, byte)
                bytesRead += byte
                val progress = bytesRead / 1024f

                withContext(Dispatchers.Main) {
                    callback.progreso(progress)
                }
            }
            val bytes = byteArrayOutputStream.toByteArray()

            // Convertir bytes en ArrayList<Parcelable>
            byteArrayInputStream = ByteArrayInputStream(bytes)
            objectInputStream = ObjectInputStream(byteArrayInputStream)
            val lista = objectInputStream.readObject() as ArrayList<Parcelable>

            // Llamar callback en el hilo principal
            withContext(Dispatchers.Main) {
                callback.onMessageReceived(lista)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.IO) {
                // Cerrar streams y socket
                inputStream?.close()
                byteArrayOutputStream?.close()
                byteArrayInputStream?.close()
                objectInputStream?.close()
                socket?.close()
            }
        }
    }
}
