package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.CircuitoDAO
import com.example.demo.model.Circuito

class CircuitosRepository(private val circuitoDao: CircuitoDAO) {
    val allCircuitos: LiveData<List<Circuito>> = circuitoDao.getAll()
    fun insertCircuito(circuito: Circuito) {
        circuitoDao.insert(circuito)
    }

    fun updateCircuito(circuito: Circuito) {
        circuitoDao.update(circuito)
    }
}