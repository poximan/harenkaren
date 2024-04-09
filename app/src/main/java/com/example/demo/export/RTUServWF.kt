package com.example.demo.export

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class RTUServWF(private val serverIp: String) {

    companion object {
        private const val TAG = "RTUServer"
        private const val PORT = 8888
    }

    fun sendData(data: String) {
        SendDataTask(data).execute()
    }

    private inner class SendDataTask(private val data: String) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val socket = Socket(serverIp, PORT)
                Log.d(TAG, "Connected to server at $serverIp:$PORT")

                val output = ObjectOutputStream(socket.getOutputStream())
                output.writeObject(data)
                output.flush()

                output.close()
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error sending data: ${e.message}")
            }
            return null
        }
    }
}