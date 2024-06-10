package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial
import java.util.UUID

class UnSocRepository(private val dao: UnSocDAO) {

    fun insert(unidSocial: UnidSocial) {
        dao.insertConUUID(unidSocial)
    }

    fun update(unidSocial: UnidSocial) {
        dao.update(unidSocial)
    }

    fun readUnico(id: UUID): UnidSocial {
        return dao.getUnSocByUUID(id)
    }

    fun readConFK(idRecorr: UUID): List<UnidSocial> {
        val listaIntermedia = dao.getUnSocByRecorrId(idRecorr)
        // TODO hacer conversion
        return listaIntermedia
    }

    fun getMaxRegistro(idRecorr: UUID): Int {
        return dao.getMaxRegistro(idRecorr)
    }

    /*
    SQL para graficar
     */
    fun readSumUnSocByDiaId(id: UUID): UnidSocial? {
        return try {
            dao.getTotalByDiaId(id)
        } catch (e: NullPointerException) {
            null
        }
    }

    /*
    SQL para graficar
     */
    fun readSumUnSocByRecorrId(id: UUID): UnidSocial? {
        return try {
            dao.getTotalByRecorrId(id)
        } catch (e: NullPointerException) {
            null
        }
    }

    fun readSumTotal(): UnidSocial {
        return dao.getSumTotal()
    }

    private fun convertirAData(list: List<UnidSocial>): LiveData<List<UnidSocial>> {
        val liveData = MutableLiveData<List<UnidSocial>>()
        liveData.postValue(list)
        return liveData
    }

    fun readAsynConFK(idRecorr: UUID): List<UnidSocial> {
        return dao.getUnSocByRecorrId(idRecorr)
    }
}

