package com.example.demo.compartir.importar

import android.os.Parcelable

interface RegistroDistribuible {
    fun onMessageReceived(message: ArrayList<Parcelable>)
    fun progreso(valor: Float)
}