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

    /*
    cuando se da de alta una entidad que existe en otro dispositivo y fue
    importada a este mediante una herramienta de transferencia, se debe
    adecuar su contador de instancias al contexto de la BD destino
    */
    fun insertConUltInst(elem: Recorrido) {
        val ultimaInstancia = getUltimaInstancia(elem.diaId) ?: 0
        elem.contadorInstancias = ultimaInstancia + 1
        insert(elem)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recorrido: Recorrido)

    @Query("SELECT MAX(cont_inst) FROM recorrido WHERE id_dia = :idDia")
    fun getUltimaInstancia(idDia: UUID): Int?

    @Query("DELETE FROM recorrido")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM recorrido")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Recorrido): Int

    @Transaction
    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>) {
        listaEntidadesPlanas.forEach { entidadPlana ->
            val recorrido = entidadPlana.getRecorrido()

            val filasActualizadas = update(recorrido)
            if (filasActualizadas == 0) {
                insertConUltInst(recorrido)
            }
        }
    }
}

