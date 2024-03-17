package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

import com.example.demo.model.UnidSocial

@Dao
interface UnSocDAO {

    @Query("SELECT * from unidsocial ORDER BY id DESC")
    fun getAll(): LiveData<List<UnidSocial>>

    /*
    JOIN es la condicion de union de las tablas, mientras que WHERE es el filtro de filas luego del JOIN
    es necesario hacer el join para respetar el metodo abstracto de retorno 'Map<Recorrido, List<UnidSocial>>'

    @Transaction
    @Query("SELECT * FROM recorrido JOIN unidsocial ON recorrido.id = unidsocial.id_recorrido WHERE unidsocial.id_recorrido = :idRecorrido")
    fun getUnidSocialByRecorridoId(idRecorrido: Int): Map<Recorrido, List<UnidSocial>>
    */
    @Transaction
    @Query("SELECT * FROM unidsocial WHERE unidsocial.id_recorrido = :idRecorrido")
    fun getUnSocByRecorrId(idRecorrido: Int): List<UnidSocial>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(unidSocial: UnidSocial)

    @Query("DELETE FROM unidsocial")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM unidsocial")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(unidSocial: UnidSocial)
}

