package com.example.demo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.RecorrConUnSoc
import com.example.demo.model.UnidSocial

class UnSocRepository(private val unSocDao: UnSocDAO) {

    val unSocListAll: LiveData<List<UnidSocial>> = unSocDao.getAll()

    private val unSocListJoin: LiveData<List<RecorrConUnSoc>> = unSocDao.getRecorrConUnSocList()

    val unSocList: LiveData<List<UnidSocial>> = unSocListJoin.map { list ->
        list.flatMap { recorrConUnSoc -> recorrConUnSoc.listaUnidSociales }
    }

    fun insert(unidSocial: UnidSocial) {
        unSocDao.insert(unidSocial)
    }
    fun update(unidSocial: UnidSocial) {
        unSocDao.update(unidSocial)
    }
}

