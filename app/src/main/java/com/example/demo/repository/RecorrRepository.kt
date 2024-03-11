package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido

class RecorrRepository(private val recorrDao: RecorrDAO) {
    val recorrList: LiveData<List<Recorrido>> = recorrDao.getAll()

    fun insert(recorrido: Recorrido) {
        recorrDao.insert(recorrido)
    }

    fun update(recorrido: Recorrido) {
        recorrDao.update(recorrido)
    }
}