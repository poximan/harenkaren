package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial

class UnSocRepository(private val unSocDao: UnSocDAO) : RepositoryCRUD<UnidSocial> {

    val unSocListAll: LiveData<List<UnidSocial>> = unSocDao.getAll()

    fun insert(unidSocial: UnidSocial) {
        unSocDao.insert(unidSocial)
    }
    fun update(unidSocial: UnidSocial) {
        unSocDao.update(unidSocial)
    }

    override fun read(idRecorrido: Int): LiveData<List<UnidSocial>> {
        val listaIntermedia = unSocDao.getUnSocByRecorrId(idRecorrido)
        return convertirAData(listaIntermedia)
    }

    private fun convertirAData(list: List<UnidSocial>): LiveData<List<UnidSocial>> {
        val liveData = MutableLiveData<List<UnidSocial>>()
        liveData.postValue(list)
        return liveData
    }
}

