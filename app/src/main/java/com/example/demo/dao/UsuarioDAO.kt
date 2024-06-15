package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.demo.model.Usuario

@Dao
interface UsuarioDAO {
    @Query("SELECT * from usuario ORDER BY id ASC")
    fun getAll(): LiveData<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(elem: Usuario)

    @Query("DELETE FROM usuario")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM usuario")
    fun getCount(): Int

    @Update
    fun update(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE id = :id LIMIT 1")
    fun getUsuarioById(id: Int): Usuario

    @Query("SELECT * FROM usuario WHERE email = :email AND pass = :pass")
    fun getUsuario(email: String, pass: String): List<Usuario>

    @Query("INSERT INTO usuario (email, pass, esAdmin) VALUES (:email, :pass, :esAdmin)")
    fun create(email: String, pass: String, esAdmin: Boolean = true): Long
}

