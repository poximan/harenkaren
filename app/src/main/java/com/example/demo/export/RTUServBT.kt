package com.example.demo.export

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class RTUServBT(private val bluetoothAdapter: BluetoothAdapter) {

    @SuppressLint("MissingPermission")
    fun connectToMTU(lista: ArrayList<Parcelable>, mtuMacAddress: String) {
        val mtuDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(mtuMacAddress)
        if (mtuDevice == null) {
            // Manejar error: dispositivo maestro no encontrado
            return
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(lista)
        val bytes = byteArrayOutputStream.toByteArray()

        try {
            val socket: BluetoothSocket = mtuDevice.createRfcommSocketToServiceRecord(GestorBT.BLUETOOTH_UUID)
            // TODO ¿hace falta? socket.connect()
            // Enviar el mensaje al dispositivo maestro
            val outputStream = socket.outputStream
            outputStream.write(bytes)
            outputStream.flush()
            outputStream.close()
            println("ArrayList enviado con éxito.")
        } catch (e: IOException) {
            // Manejar error de conexión
        }
    }
}