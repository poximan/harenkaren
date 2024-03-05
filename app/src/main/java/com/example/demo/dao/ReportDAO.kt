package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.demo.model.Censo


@Dao
interface ReportDAO {
    @Query("SELECT * from censo_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Censo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(censo: Censo)

    @Query("DELETE FROM censo_table")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM censo_table")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(censo: Censo)
}

