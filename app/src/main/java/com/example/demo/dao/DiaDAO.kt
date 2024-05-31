package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.DevFragment
import com.example.demo.model.Dia
import com.example.demo.model.EntidadesPlanas
import com.example.demo.servicios.GestorUUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Dao
interface DiaDAO {
    @Query("SELECT * from dia ORDER BY id ASC")
    fun getAll(): LiveData<List<Dia>>

    @Query("SELECT * from dia WHERE id = :idDia")
    fun getDiaByUUID(idDia: UUID): Dia

    /*
    cuando se da de alta una entidad que no existio nunca en este ni en ningun
    otro dispositivo, se usa insertConUUID(elem) para asignar UUID unico.
    */
    fun insertConUUID(elem: Dia): UUID {
        if (elem.id == DevFragment.UUID_NULO)
            elem.id = GestorUUID.obtenerUUID()

        insertConUltInst(elem)
        return elem.id
    }

    /*
    cuando se da de alta una entidad que existe en otro dispositivo y fue
    importada a este mediante una herramienta de transferencia, el UUID
    ya se calculo antes y no debe ser reemplazado, entonces se usa
    insertConUltInst(elem) para adecuar su contador de instancias
    al contexto de la BD donde será insertada la entidad
    */
    private fun insertConUltInst(elem: Dia) {
        val ultimaInstancia = getUltimaInstancia() ?: 0
        elem.orden = ultimaInstancia + 1
        insert(elem)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(elem: Dia)

    @Query("SELECT MAX(orden) FROM dia")
    fun getUltimaInstancia(): Int?

    @Query("DELETE FROM dia")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM dia")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Dia): Int

    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>): Int {
        var ultimoID: UUID? = null
        var insertsEfectivos = 0

        listaEntidadesPlanas.forEach { entidadPlana ->
            val dia = entidadPlana.getDia()

            if(dia.id != ultimoID) {
                val existe = getDiaByUUID(dia.id)

                if (existe == null) {
                    insertConUltInst(dia)
                    insertsEfectivos += 1
                }
                ultimoID = dia.id
            }
        }
        return insertsEfectivos
    }
}

