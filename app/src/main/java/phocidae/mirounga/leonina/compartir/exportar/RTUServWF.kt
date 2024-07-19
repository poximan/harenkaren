package phocidae.mirounga.leonina.compartir.exportar

import android.os.AsyncTask
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class RTUServWF(private val ip: String, private val port: Int) {

    fun sendData(lista: ArrayList<Parcelable>) {
        SendDataTask(lista).execute()
    }

    private inner class SendDataTask(private val lista: ArrayList<Parcelable>) :
        AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(lista)
            val bytes = byteArrayOutputStream.toByteArray()

            // Establecer conexión con el servidor y enviar los datos
            try {
                val socket = Socket(ip, port)
                val outputStream = socket.outputStream
                outputStream.write(bytes)
                outputStream.flush()
                outputStream.close()
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}