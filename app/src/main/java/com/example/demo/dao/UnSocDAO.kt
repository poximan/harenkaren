package com.example.demo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.demo.DevFragment
import com.example.demo.exception.UUIDRepetidoException
import com.example.demo.model.EntidadesPlanas
import com.example.demo.model.UnidSocial
import com.example.demo.servicios.GestorUUID
import java.util.UUID

@Dao
interface UnSocDAO {

    @Query("SELECT * from unidsocial ORDER BY id DESC")
    fun getAll(): List<UnidSocial>

    @Query("SELECT * FROM unidsocial WHERE unidsocial.id = :id")
    fun getUnSocByUUID(id: UUID): UnidSocial

    @Transaction
    @Query("SELECT * FROM unidsocial WHERE unidsocial.id_recorrido = :idRecorrido")
    fun getUnSocByRecorrId(idRecorrido: UUID): List<UnidSocial>

    @Query("SELECT * FROM unidsocial WHERE substr(date, 0, 5) = :anio")
    fun getAllPorAnio(anio: String): List<UnidSocial>

    /*
   cuando se da de alta una entidad que no existio nunca en este ni en ningun
   otro dispositivo, se usa insertConUUID(elem) para asignar UUID unico.
   */
    fun insertConUUID(elem: UnidSocial): UUID {
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
    private fun insertConUltInst(elem: UnidSocial) {
        val ultimaInstancia = getUltimaInstancia(elem.recorrId) ?: 0
        elem.orden = ultimaInstancia + 1
        insert(elem)
    }

    @Query("SELECT MAX(orden) FROM unidsocial WHERE id_recorrido = :idRecorr")
    fun getUltimaInstancia(idRecorr: UUID): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(unidSocial: UnidSocial)

    @Query("DELETE FROM unidsocial")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM unidsocial")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(unidSocial: UnidSocial)

    fun insertarDesnormalizado(listaEntidadesPlanas: List<EntidadesPlanas>): Int {
        var insertsEfectivos = 0
        listaEntidadesPlanas.forEach { entidadPlana ->

            val unSoc = entidadPlana.getUnidSocial()
            val existe = getUnSocByUUID(unSoc.id)

            if (existe == null) {
                insertConUltInst(unSoc)
                insertsEfectivos += 1
            } else
                throw UUIDRepetidoException()
        }
        return insertsEfectivos
    }

    @Query(
        "SELECT \n" +
                "    dia.id_celular AS celular_id,\n" +
                "    dia.id AS dia_id,\n" +
                "    dia.orden AS dia_orden,\n" +
                "    dia.fecha AS dia_fecha,\n" +
                "    -- recorrido\n" +
                "    recorrido.id AS recorr_id,\n" +
                "    recorrido.id_dia AS recorr_id_dia,\n" +
                "    recorrido.orden AS recorr_orden,\n" +
                "    recorrido.observador,\n" +
                "    recorrido.fecha_ini AS recorr_fecha_ini,\n" +
                "    recorrido.fecha_fin AS recorr_fecha_fin,\n" +
                "    recorrido.latitud_ini AS recorr_latitud_ini,\n" +
                "    recorrido.longitud_ini AS recorr_longitud_ini,\n" +
                "    recorrido.latitud_fin AS recorr_latitud_fin,\n" +
                "    recorrido.longitud_fin AS recorr_longitud_fin,\n" +
                "    recorrido.area_recorrida,\n" +
                "    recorrido.meteo,\n" +
                "    recorrido.marea,\n" +
                "    -- unidad social\n" +
                "    unidsocial.id AS unsoc_id,\n" +
                "    unidsocial.id_recorrido AS unsoc_id_recorr,\n" +
                "    unidsocial.orden AS unsoc_orden,\n" +
                "    unidsocial.pto_observacion,\n" +
                "    unidsocial.ctx_social,\n" +
                "    unidsocial.tpo_sustrato,\n" +
                "    -- Propiedades VIVIOS\n" +
                "    unidsocial.v_alfa_s4ad,\n" +
                "    unidsocial.v_alfa_sams,\n" +
                "    unidsocial.v_hembras_ad,\n" +
                "    unidsocial.v_crias,\n" +
                "    unidsocial.v_destetados,\n" +
                "    unidsocial.v_juveniles,\n" +
                "    unidsocial.v_s4ad_perif,\n" +
                "    unidsocial.v_s4ad_cerca,\n" +
                "    unidsocial.v_s4ad_lejos,\n" +
                "    unidsocial.v_otros_sams_perif,\n" +
                "    unidsocial.v_otros_sams_cerca,\n" +
                "    unidsocial.v_otros_sams_lejos,\n" +
                "    -- Propiedades MUERTOS\n" +
                "    unidsocial.m_alfa_s4ad,\n" +
                "    unidsocial.m_alfa_sams,\n" +
                "    unidsocial.m_hembras_ad,\n" +
                "    unidsocial.m_crias,\n" +
                "    unidsocial.m_destetados,\n" +
                "    unidsocial.m_juveniles,\n" +
                "    unidsocial.m_s4ad_perif,\n" +
                "    unidsocial.m_s4ad_cerca,\n" +
                "    unidsocial.m_s4ad_lejos,\n" +
                "    unidsocial.m_otros_sams_perif,\n" +
                "    unidsocial.m_otros_sams_cerca,\n" +
                "    unidsocial.m_otros_sams_lejos,\n" +
                "    -- Propiedades adicionales\n" +
                "    unidsocial.date AS unsoc_fecha,\n" +
                "    unidsocial.latitud AS unsoc_latitud,\n" +
                "    unidsocial.longitud AS unsoc_longitud,\n" +
                "    unidsocial.photo_path,\n" +
                "    unidsocial.comentario\n" +
                "FROM \n" +
                "    dia\n" +
                "INNER JOIN \n" +
                "    recorrido ON dia.id = recorrido.id_dia\n" +
                "INNER JOIN \n" +
                "    unidsocial ON recorrido.id = unidSocial.id_recorrido"
    )
    fun getUnSocDesnormalizado(): List<EntidadesPlanas>

    /*
    ---------------------- GRAFICAS ----------------------
    ------------------------------------------------------
     */

    @Query(
        "SELECT MAX(total) AS maximo_total FROM (\n" +
                "    SELECT \n" +
                "        v_alfa_s4ad + v_alfa_sams + v_hembras_ad + v_crias + v_destetados + v_juveniles +\n" +
                "        v_s4ad_perif + v_s4ad_cerca + v_s4ad_lejos + v_otros_sams_perif + v_otros_sams_cerca + v_otros_sams_lejos +\n" +
                "        m_alfa_s4ad + m_alfa_sams + m_hembras_ad + m_crias + m_destetados + m_juveniles +\n" +
                "        m_s4ad_perif + m_s4ad_cerca + m_s4ad_lejos + m_otros_sams_perif + m_otros_sams_cerca + m_otros_sams_lejos AS total\n" +
                "    FROM unidsocial\n" +
                "    WHERE id_recorrido = :idRecorr\n" +
                "    )"
    )
    fun getMaxRegistro(idRecorr: UUID): Int

    @Query(
        "SELECT \n" +
                "   unidsocial.id, id_recorrido, unidsocial.orden,\n" +
                "   pto_observacion, ctx_social, tpo_sustrato,\n" +
                "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
                "   SUM(v_alfa_sams) AS v_alfa_sams,\n" +
                "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
                "   SUM(v_crias) AS v_crias,\n" +
                "   SUM(v_destetados) AS v_destetados,\n" +
                "   SUM(v_juveniles) AS v_juveniles,\n" +
                "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
                "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
                "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
                "   SUM(v_otros_sams_perif) AS v_otros_sams_perif,\n" +
                "   SUM(v_otros_sams_cerca) AS v_otros_sams_cerca,\n" +
                "   SUM(v_otros_sams_lejos) AS v_otros_sams_lejos,\n" +
                "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
                "   SUM(m_alfa_sams) AS m_alfa_sams,\n" +
                "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
                "   SUM(m_crias) AS m_crias,\n" +
                "   SUM(m_destetados) AS m_destetados,\n" +
                "   SUM(m_juveniles) AS m_juveniles,\n" +
                "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
                "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
                "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
                "   SUM(m_otros_sams_perif) AS m_otros_sams_perif,\n" +
                "   SUM(m_otros_sams_cerca) AS m_otros_sams_cerca,\n" +
                "   SUM(m_otros_sams_lejos) AS m_otros_sams_lejos,\n" +
                "   date, latitud, longitud, photo_path, comentario\n" +
                "FROM \n" +
                "   unidsocial\n" +
                "JOIN\n" +
                "   recorrido ON unidsocial.id_recorrido = recorrido.id\n" +
                "JOIN\n" +
                "   dia ON recorrido.id_dia = dia.id\n" +
                "WHERE \n" +
                "   dia.id = :id"
    )
    fun getTotalByDiaId(id: UUID): UnidSocial

    @Query(
        "SELECT \n" +
                "   id, id_recorrido, orden,\n" +
                "   pto_observacion, ctx_social, tpo_sustrato,\n" +
                "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
                "   SUM(v_alfa_sams) AS v_alfa_sams,\n" +
                "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
                "   SUM(v_crias) AS v_crias,\n" +
                "   SUM(v_destetados) AS v_destetados,\n" +
                "   SUM(v_juveniles) AS v_juveniles,\n" +
                "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
                "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
                "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
                "   SUM(v_otros_sams_perif) AS v_otros_sams_perif,\n" +
                "   SUM(v_otros_sams_cerca) AS v_otros_sams_cerca,\n" +
                "   SUM(v_otros_sams_lejos) AS v_otros_sams_lejos,\n" +
                "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
                "   SUM(m_alfa_sams) AS m_alfa_sams,\n" +
                "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
                "   SUM(m_crias) AS m_crias,\n" +
                "   SUM(m_destetados) AS m_destetados,\n" +
                "   SUM(m_juveniles) AS m_juveniles,\n" +
                "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
                "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
                "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
                "   SUM(m_otros_sams_perif) AS m_otros_sams_perif,\n" +
                "   SUM(m_otros_sams_cerca) AS m_otros_sams_cerca,\n" +
                "   SUM(m_otros_sams_lejos) AS m_otros_sams_lejos,\n" +
                "   date, latitud, longitud, photo_path, comentario \n" +
                "FROM unidsocial\n" +
                "WHERE \n" +
                "   unidsocial.id_recorrido = :idRecorr"
    )
    fun getTotalByRecorrId(idRecorr: UUID): UnidSocial

    @Query(
        "SELECT \n" +
                "   id, id_recorrido, orden,\n" +
                "   pto_observacion, ctx_social, tpo_sustrato,\n" +
                "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
                "   SUM(v_alfa_sams) AS v_alfa_sams,\n" +
                "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
                "   SUM(v_crias) AS v_crias,\n" +
                "   SUM(v_destetados) AS v_destetados,\n" +
                "   SUM(v_juveniles) AS v_juveniles,\n" +
                "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
                "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
                "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
                "   SUM(v_otros_sams_perif) AS v_otros_sams_perif,\n" +
                "   SUM(v_otros_sams_cerca) AS v_otros_sams_cerca,\n" +
                "   SUM(v_otros_sams_lejos) AS v_otros_sams_lejos,\n" +
                "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
                "   SUM(m_alfa_sams) AS m_alfa_sams,\n" +
                "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
                "   SUM(m_crias) AS m_crias,\n" +
                "   SUM(m_destetados) AS m_destetados,\n" +
                "   SUM(m_juveniles) AS m_juveniles,\n" +
                "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
                "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
                "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
                "   SUM(m_otros_sams_perif) AS m_otros_sams_perif,\n" +
                "   SUM(m_otros_sams_cerca) AS m_otros_sams_cerca,\n" +
                "   SUM(m_otros_sams_lejos) AS m_otros_sams_lejos,\n" +
                "   date, latitud, longitud, photo_path, comentario \n" +
                "FROM \n" +
                "    unidsocial\n"
    )
    fun getSumTotal(): UnidSocial
}