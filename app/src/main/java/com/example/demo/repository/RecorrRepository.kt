package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido

class RecorrRepository(private val recorrDao: RecorrDAO) {

    val recorrListAll: LiveData<List<Recorrido>> = recorrDao.getAll()

    fun insert(recorrido: Recorrido) {
        recorrDao.insert(recorrido)
    }
    fun update(recorrido: Recorrido) {
        recorrDao.update(recorrido)
    }

    fun read(idDia: Int): LiveData<List<Recorrido>> {
        val listaIntermedia = recorrDao.getRecorrByDiaId(idDia)
        return convertirAData(listaIntermedia)
    }

    private fun convertirAData(list: List<Recorrido>): LiveData<List<Recorrido>> {
        val liveData = MutableLiveData<List<Recorrido>>()
        liveData.postValue(list)
        return liveData
    }
}