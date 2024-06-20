package com.example.demo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.demo.database.DevFragment
import com.example.demo.model.EntidadesPlanas
import com.example.demo.model.Recorrido
import com.example.demo.servicios.GestorUUID
import java.util.UUID

@Dao
interface RecorrDAO {

    @Query("SELECT * from recorrido ORDER BY id DESC")
    fun getAll(): LiveData<List<Recorrido>>

    @Query("SELECT * FROM recorrido WHERE recorrido.id = :id")
    fun getRecorrByUUID(id: UUID): Recorrido

    @Query(
        """
        SELECT * 
        FROM recorrido
        WHERE recorrido.id_dia = :idDia 
    """
    )
    fun getRecorrByDiaId(idDia: UUID): List<Recorrido>

    @Query(
        """
        SELECT fecha 
        FROM dia
        WHERE dia.id = :idDia 
    """
    )
    fun getFechaObservada(idDia: UUID): String

    /*
   cuando se da de alta una entidad que no existio nunca en este ni en ningun
   otro dispositivo, se usa insertConUUID(elem) para asignar UUID unico.
   */
    fun insertConUUID(elem: Recorrido): UUID {
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
    private fun insertConUltInst(elem: Recorrido) {
        val ultimaInstancia = getUltimaInstancia(elem.diaId) ?: 0
        elem.orden = ultimaInstancia + 1
        insert(elem)
    }

    @Query("SELECT MAX(orden) FROM recorrido WHERE id_dia = :idDia")
    fun getUltimaInstancia(idDia: UUID): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recorrido: Recorrido)

    @Query("DELETE FROM recorrido")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM recorrido")
    fun getCount(): Int

    @Update
    fun update(recorrido: Recorrido): Int

    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>): Int {
        var insertsEfectivos = 0

        listaEntidadesPlanas.forEach { entidadPlana ->

            val recorr = entidadPlana.getRecorrido()
            val existe = getRecorrByUUID(recorr.id)

            if (existe == null) {
                insertConUltInst(recorr)
                insertsEfectivos += 1
            }
        }
        return insertsEfectivos
    }
}

