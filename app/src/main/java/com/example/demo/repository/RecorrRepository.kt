package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido

class RecorrRepository(private val dao: RecorrDAO) : Consultable<Recorrido> {

    val recorrListAll: LiveData<List<Recorrido>> = dao.getAll()

    fun insert(recorrido: Recorrido) {
        dao.insert(recorrido)
    }
    fun update(recorrido: Recorrido) {
        dao.update(recorrido)
    }

    override fun readConFK(id: Int): LiveData<List<Recorrido>> {
        val listaIntermedia = dao.getRecorrByDiaId(id)
        return convertirAData(listaIntermedia)
    }

    private fun convertirAData(list: List<Recorrido>): LiveData<List<Recorrido>> {
        val liveData = MutableLiveData<List<Recorrido>>()
        liveData.postValue(list)
        return liveData
    }

    fun readAsynConFK(id: Int): List<Recorrido> {
        return dao.getRecorrByDiaId(id)
    }
}