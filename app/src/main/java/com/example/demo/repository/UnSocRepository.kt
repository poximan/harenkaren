package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial

class UnSocRepository(private val unSocDao: UnSocDAO) {

    val unSocList: LiveData<List<UnidSocial>> = unSocDao.getRecorrConUnSocList()

    fun insert(unidSocial: UnidSocial) {
        unSocDao.insert(unidSocial)
    }
    fun update(unidSocial: UnidSocial) {
        unSocDao.update(unidSocial)
    }
}