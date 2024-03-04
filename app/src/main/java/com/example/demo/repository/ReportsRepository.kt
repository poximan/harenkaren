package com.example.demo.repository

import androidx.lifecycle.LiveData
import com.example.demo.dao.ReportDAO
import com.example.demo.model.Censo

class ReportsRepository(private val reportDao: ReportDAO) {
    val allReports: LiveData<List<Censo>> = reportDao.getReports()
    fun insertReport(censo: Censo) {
        reportDao.insertReport(censo)
    }

    fun updateReport(censo: Censo) {
        reportDao.updateReport(censo)
    }
}