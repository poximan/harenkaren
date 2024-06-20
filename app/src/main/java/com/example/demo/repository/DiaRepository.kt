package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.DiaDAO
import com.example.demo.model.Dia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaRepository(private val diaDao: DiaDAO) {

    val diaListAll: LiveData<List<Dia>> = diaDao.getAll()

    fun getAnios(): List<Int> {
        return diaDao.getAnios()
    }

    fun insert(elem: Dia) {
        diaDao.insertConUUID(elem)
    }

    fun update(elem: Dia) {
        diaDao.update(elem)
    }

    fun delete(dia: Dia) {
        CoroutineScope(Dispatchers.IO).launch {
            diaDao.delete(dia)
        }
    }

    fun getDias(anio: Int): List<Dia> {
        return diaDao.getDias(anio)
    }
}