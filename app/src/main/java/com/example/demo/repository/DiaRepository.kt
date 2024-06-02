package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.DiaDAO
import com.example.demo.model.Dia
import java.util.UUID

class DiaRepository(private val diaDao: DiaDAO) {

    val diaListAll: LiveData<List<Dia>> = diaDao.getAll()

    fun insert(elem: Dia) {
        diaDao.insertConUUID(elem)
    }

    fun update(elem: Dia) {
        diaDao.update(elem)
    }

    fun contarUnSocPorDia(id: UUID): Int {
        return diaDao.contarUnSocPorDia(id)
    }
}