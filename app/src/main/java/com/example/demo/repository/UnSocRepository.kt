package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.ReportDAO
import com.example.demo.model.UnidSocial

class UnSocRepository(private val reportDao: ReportDAO) {
    val allReports: LiveData<List<UnidSocial>> = reportDao.getAll()
    fun insert(unidSocial: UnidSocial) {
        reportDao.insert(unidSocial)
    }

    fun update(unidSocial: UnidSocial) {
        reportDao.update(unidSocial)
    }
}