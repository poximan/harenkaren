package com.example.demo.repository

import androidx.lifecycle.LiveData

interface Consultable<T> {

    fun readConFK(id: Int): LiveData<List<T>>
}
