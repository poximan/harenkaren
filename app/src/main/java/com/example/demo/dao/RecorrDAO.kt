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

    @Query("SELECT * FROM recorrido WHERE " +
            "id_dia = :diaId AND " +
            "observador = :observador AND " +
            "fecha_ini = :fechaIni AND " +
            "fecha_fin = :fechaFin AND " +
            "latitud_ini = :latitudIni AND " +
            "longitud_ini = :longitudIni AND " +
            "latitud_fin = :latitudFin AND " +
            "longitud_fin = :longitudFin AND " +
            "area_recorrida = :areaRecorrida AND " +
            "meteo = :meteo AND " +
            "marea = :marea")
    fun getRecorridoByCampos(diaId: UUID, observador: String,
                             fechaIni: String, fechaFin: String,
                             latitudIni: Double, longitudIni: Double,
                             latitudFin: Double, longitudFin: Double,
                             areaRecorrida: String, meteo: String, marea: String): Recorrido

    @Query("SELECT * FROM recorrido WHERE recorrido.id = :id")
    fun getRecorrById(id: Int): Recorrido

    @Transaction
    @Query("SELECT * FROM recorrido WHERE recorrido.id_dia = :idDia")
    fun getRecorrByDiaId(idDia: UUID): List<Recorrido>

    /*
    cuando se da de alta una entidad que existe en otro dispositivo y fue
    importada a este mediante una herramienta de transferencia, se debe
    adecuar su contador de instancias al contexto de la BD destino
    */
    fun insertConUltInst(elem: Recorrido): Int {
        val ultimaInstancia = getUltimaInstancia(elem.diaId) ?: 0
        elem.orden = ultimaInstancia + 1
        return insert(elem).toInt()
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recorrido: Recorrido): Long

    @Query("SELECT MAX(orden) FROM recorrido WHERE id_dia = :idDia")
    fun getUltimaInstancia(idDia: UUID): Int?

    @Query("DELETE FROM recorrido")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM recorrido")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(recorrido: Recorrido): Int

    @Transaction
    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>): Int {
        var insertsEfectivos = 0
        listaEntidadesPlanas.forEachIndexed { indice, entidadPlana ->

            val recorr = entidadPlana.getRecorrido()
            val existe = getRecorridoByCampos(recorr.diaId, recorr.observador,
                recorr.fechaIni, recorr.fechaFin, recorr.latitudIni, recorr.longitudIni,
                recorr.latitudFin, recorr.longitudFin, recorr.areaRecorrida,
                recorr.meteo, recorr.marea)

            if (existe == null){
                recorr.id = null
                recorr.id = insertConUltInst(recorr)
                insertsEfectivos += 1
            } else {
                recorr.id = existe.id
            }
            /*
            se actualiza es lista porque luego sera consumida por los insert de unidad social,
            y es necesario que tengan las referencias de clave foranea actualizadas
            */
            listaEntidadesPlanas[indice].recorr_id = recorr.id!!
            listaEntidadesPlanas[indice].unsoc_id_recorr = recorr.id!!
        }
        return insertsEfectivos
    }
}

