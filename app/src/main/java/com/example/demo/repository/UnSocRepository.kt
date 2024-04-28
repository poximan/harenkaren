package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial

class UnSocRepository(private val dao: UnSocDAO) {

    val unSocListAll: LiveData<List<UnidSocial>> = dao.getAll()

    fun insert(unidSocial: UnidSocial) {
        dao.insertConUltInst(unidSocial)
    }
    fun update(unidSocial: UnidSocial) {
        dao.update(unidSocial)
    }

    fun readUnico(id: Int): UnidSocial {
        return dao.getUnSocById(id)
    }

    fun getMaxRegistro(idRecorr: Int): Int {
        return dao.getMaxRegistro(idRecorr)
    }

    fun readConFK(idRecorr: Int): LiveData<List<UnidSocial>> {
        val listaIntermedia = dao.getUnSocByRecorrId(idRecorr)
        return convertirAData(listaIntermedia)
    }

    fun readSumUnSocByRecorrId(id: Int): UnidSocial {
        return dao.getSumUnSocByRecorrId(id)
    }

    fun readSumUnSocByDiaId(idContInst: Int): UnidSocial {
        return dao.getTotalByDiaId(idContInst)
    }

    fun readSumTotal(): UnidSocial {
        return dao.getSumTotal()
    }

    private fun convertirAData(list: List<UnidSocial>): LiveData<List<UnidSocial>> {
        val liveData = MutableLiveData<List<UnidSocial>>()
        liveData.postValue(list)
        return liveData
    }

    fun readAsynConFK(idRecorr: Int): List<UnidSocial> {
        return dao.getUnSocByRecorrId(idRecorr)
    }
}

