package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.DevFragment
import com.example.demo.activity.MainActivity
import com.example.demo.model.Dia
import com.example.demo.model.EntidadesPlanas
import java.nio.charset.StandardCharsets
import java.util.UUID

@Dao
interface DiaDAO {
    @Query("SELECT * from dia ORDER BY id ASC")
    fun getAll(): LiveData<List<Dia>>

    /*
    cuando se da de alta una entidad que no existio nunca ni en este ni en ningun
    otro dispositivo, se pasa primero por aqui para asignar un UUID unico.

    en cambio cuando se da de alta una entidad que existe en otro dispositivo y fue
    importada a este mediante una herramienta de transferencia, el UUID
    ya se calculo antes y no debe ser reemplazado. usar directamente insert(elem)
     */
    fun insertar(elem: Dia): UUID {

        if (elem.id == DevFragment.UUID_NULO) {
            val nombre = MainActivity.obtenerAndroidID()
            val uuid =
                UUID.nameUUIDFromBytes("$elem.id:$nombre".toByteArray(StandardCharsets.UTF_8))
                elem.id = uuid
        }
        insert(elem)
        return elem.id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(elem: Dia)

    @Query("DELETE FROM dia")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM dia")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Dia): Int

    @Transaction
    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>){

        listaEntidadesPlanas.forEach { entidadPlana ->
            val dia = entidadPlana.getDia()

            val filasActualizadas = update(dia)
            if (filasActualizadas == 0) {
                insert(dia)
            }
        }
    }
}

