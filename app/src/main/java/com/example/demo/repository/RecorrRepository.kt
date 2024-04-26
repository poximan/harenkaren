package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import java.util.UUID

class RecorrRepository(private val dao: RecorrDAO) {

    val recorrListAll: LiveData<List<Recorrido>> = dao.getAll()

    fun insert(recorrido: Recorrido) {
        dao.insertConUltInst(recorrido)
    }
    fun update(recorrido: Recorrido) {
        dao.update(recorrido)
    }

    fun readUnico(id: Int): Recorrido {
        return dao.getRecorrById(id)
    }

    fun readConFK(id: UUID): LiveData<List<Recorrido>> {
        val listaIntermedia = dao.getRecorrByDiaId(id)
        return convertirAData(listaIntermedia)
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