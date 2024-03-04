package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.model.Circuito


@Dao
interface CircuitoDAO {
    @Query("SELECT * from circuito_table ORDER BY id DESC")
    fun getReports(): LiveData<List<Circuito>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReport(circuito: Circuito)

    @Query("DELETE FROM circuito_table")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM circuito_table")
    fun getCount(): Int

    @Transaction
    @Update
    fun updateReport(circuito: Circuito)
}

