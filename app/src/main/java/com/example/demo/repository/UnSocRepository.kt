package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial

class UnSocRepository(private val dao: UnSocDAO) : Consultable<UnidSocial> {

    val unSocListAll: LiveData<List<UnidSocial>> = dao.getAll()

    fun insert(unidSocial: UnidSocial) {
        dao.insert(unidSocial)
    }
    fun update(unidSocial: UnidSocial) {
        dao.update(unidSocial)
    }

    fun readUnico(id: Int): UnidSocial {
        return dao.getUnSocById(id)
    }

    override fun readConFK(id: Int): LiveData<List<UnidSocial>> {
        val listaIntermedia = dao.getUnSocByRecorrId(id)
        return convertirAData(listaIntermedia)
    }

    fun readSumUnSocByRecorrId(id: Int): UnidSocial {
        return dao.getSumUnSocByRecorrId(id)
    }

    fun readSumUnSocByDiaId(id: Int): UnidSocial {
        return dao.getSumUnSocByDiaId(id)
    }

    fun readSumTotal(): UnidSocial {
        return dao.getSumTotal()
    }

    private fun convertirAData(list: List<UnidSocial>): LiveData<List<UnidSocial>> {
        val liveData = MutableLiveData<List<UnidSocial>>()
        liveData.postValue(list)
        return liveData
    }
}

