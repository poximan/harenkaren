package com.example.demo.export

import android.os.AsyncTask
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class RTUServWF(private val serverIp: String) {

    companion object {
        private const val TAG = "RTUServer"
        private const val PORT = 8888
    }

    fun sendData(lista: ArrayList<Parcelable>) {
        SendDataTask(lista).execute()
    }

    private inner class SendDataTask(private val lista: ArrayList<Parcelable>) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(lista)
            val bytes = byteArrayOutputStream.toByteArray()

            // Establecer conexión con el servidor y enviar los datos
            try {
                val socket = Socket(serverIp, PORT)
                val outputStream = socket.outputStream
                outputStream.write(bytes)
                outputStream.flush()
                outputStream.close()
                socket.close()
                println("ArrayList enviado con éxito.")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}