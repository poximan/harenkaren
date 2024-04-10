package com.example.demo.export

import android.os.Parcelable

interface RegistroDistribuible {
    fun onMessageReceived(message: ArrayList<Parcelable>)
}