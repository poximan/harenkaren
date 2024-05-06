package com.example.demo.compartir.importar

import android.os.AsyncTask
import android.os.Parcelable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.net.ServerSocket

class MTUClienteWF(private val callback: RegistroDistribuible) {

    companion object {
        private const val PORT = 8888
    }

    fun startListening() {
        ServerTask().execute()
    }

    private inner class ServerTask() : AsyncTask<Void, ArrayList<Parcelable>, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            var lista: ArrayList<Parcelable>? = null
            try {
                val serverSocket = ServerSocket(PORT)
                println("Esperando conexión...")
                val socket = serverSocket.accept()
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

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            if (lista != null) {
                publishProgress(lista)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: ArrayList<Parcelable>?) {
            super.onProgressUpdate(*values)
            values.firstOrNull()?.let {
                callback.onMessageReceived(it)
            }
        }
    }
}
