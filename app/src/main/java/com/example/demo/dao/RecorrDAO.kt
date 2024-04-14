package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.model.EntidadesPlanas
import com.example.demo.model.Recorrido
import java.util.UUID

@Dao
interface RecorrDAO {
    @Query("SELECT * from recorrido ORDER BY id DESC")
    fun getAll(): LiveData<List<Recorrido>>

    @Transaction
    @Query("SELECT * FROM recorrido WHERE recorrido.id_dia = :idDia")
    fun getRecorrByDiaId(idDia: UUID): List<Recorrido>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recorrido: Recorrido)

    @Query("DELETE FROM recorrido")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM recorrido")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Recorrido): Int

    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>) {
        listaEntidadesPlanas.forEach { entidadPlana ->
            val recorrido = entidadPlana.getRecorrido()

            val filasActualizadas = update(recorrido)
            if (filasActualizadas == 0) {
                insert(recorrido)
            }
        }
    }
}

