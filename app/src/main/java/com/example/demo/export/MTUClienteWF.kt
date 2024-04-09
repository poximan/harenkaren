package com.example.demo.export

import android.os.AsyncTask
import android.util.Log
import com.example.demo.model.UnidSocial
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.net.ServerSocket

class MTUClienteWF(private val onDataReceived: (String) -> Unit) {

    companion object {
        private const val TAG = "MTUConcentrator"
        private const val PORT = 8888
    }

    private var serverSocket: ServerSocket? = null

    fun startListening() {
        ServerTask().execute()
    }

    fun stopListening() {
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing server socket: ${e.message}")
        }
    }

    private inner class ServerTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                serverSocket = ServerSocket(PORT)
                Log.d(TAG, "ServerSocket started. Listening on port $PORT")

                while (true) {
                    val clientSocket = serverSocket!!.accept()
                    Log.d(TAG, "Client connected: ${clientSocket.inetAddress}")

                    val input = clientSocket.getInputStream()
                    val reader = BufferedReader(InputStreamReader(input))

                    // Lee el string enviado por el cliente
                    val receivedString = reader.readLine()

                    reader.close()
                    clientSocket.close()

                    onDataReceived(receivedString)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error creating server socket: ${e.message}")
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "Error deserializing object: ${e.message}")
            }
            return null
        }
    }
}
