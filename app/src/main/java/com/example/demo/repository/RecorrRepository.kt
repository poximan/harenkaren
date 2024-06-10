package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido
import java.util.UUID

class RecorrRepository(private val dao: RecorrDAO) {

    val recorrListAll: LiveData<List<Recorrido>> = dao.getAll()

    fun insert(recorrido: Recorrido) {
        dao.insertConUUID(recorrido)
    }

    fun update(recorrido: Recorrido) {
        dao.update(recorrido)
    }

    fun readUnico(id: UUID): Recorrido {
        return dao.getRecorrByUUID(id)
    }

    fun readConFK(id: UUID): List<Recorrido> {
        val listaIntermedia = dao.getRecorrByDiaId(id)
        // TODO hacer conversion
        return listaIntermedia
    }

    private fun convertirAData(list: List<Recorrido>): LiveData<List<Recorrido>> {
        val liveData = MutableLiveData<List<Recorrido>>()
        liveData.postValue(list)
        return liveData
    }

    fun readAsynConFK(id: UUID): List<Recorrido> {
        return dao.getRecorrByDiaId(id)
    }
}