package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.demo.model.UnidSocial


@Dao
interface ReportDAO {
    @Query("SELECT * from unidad_social_table ORDER BY id DESC")
    fun getAll(): LiveData<List<UnidSocial>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(unidSocial: UnidSocial)

    @Query("DELETE FROM unidad_social_table")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM unidad_social_table")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(unidSocial: UnidSocial)
}

