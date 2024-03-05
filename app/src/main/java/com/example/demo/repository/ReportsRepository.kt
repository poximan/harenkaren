package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.ReportDAO
import com.example.demo.model.Censo

class ReportsRepository(private val reportDao: ReportDAO) {
    val allReports: LiveData<List<Censo>> = reportDao.getAll()
    fun insertReport(censo: Censo) {
        reportDao.insert(censo)
    }

    fun updateReport(censo: Censo) {
        reportDao.update(censo)
    }
}