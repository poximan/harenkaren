package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.model.Dia

@Dao
interface DiaDAO {
    @Query("SELECT * from dia ORDER BY id ASC")
    fun getAll(): LiveData<List<Dia>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(elem: Dia)

    @Query("DELETE FROM dia")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM dia")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Dia)
}

