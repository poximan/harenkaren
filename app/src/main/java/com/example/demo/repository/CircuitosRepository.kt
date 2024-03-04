package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.CircuitoDAO
import com.example.demo.model.Circuito

class CircuitosRepository(private val circuitoDao: CircuitoDAO) {
    val allCircuitos: LiveData<List<Circuito>> = circuitoDao.getReports()
    fun insertCircuito(circuito: Circuito) {
        circuitoDao.insertReport(circuito)
    }

    fun updateCircuito(circuito: Circuito) {
        circuitoDao.updateReport(circuito)
    }
}