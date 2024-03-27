package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.model.Dia
import com.example.demo.model.Usuario

@Dao
interface UsuarioDAO {
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

    @Query("SELECT * FROM usuario WHERE id = :idUsuario LIMIT 1")
    fun getUsuarioById(idUsuario: Int): Usuario

    @Query("SELECT * FROM usuario WHERE email = :email AND pass = :pass")
    fun getUsuario(email: String, pass: String): List<Usuario>

    @Query("INSERT INTO usuario (email, pass, esAdmin) VALUES (:email, :pass, :esAdmin)")
    fun create(email: String, pass: String, esAdmin: Boolean = true): Long
}

