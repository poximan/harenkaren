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

    @Query("SELECT * FROM unidsocial WHERE unidsocial.id = :idUnSoc")
    fun getUnSocById(idUnSoc: Int): UnidSocial

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(unidSocial: UnidSocial)

    @Query("DELETE FROM unidsocial")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM unidsocial")
    fun getCount(): Int

    @Transaction
    @Update
    fun update(unidSocial: UnidSocial)

    @Query("SELECT \n" +
            "   id, id_recorrido,\n" +
            "   pto_observacion, ctx_social, tpo_sustrato ,\n" +
            "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
            "   SUM(v_alfa_otros_sa) AS v_alfa_otros_sa,\n" +
            "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
            "   SUM(v_crias) AS v_crias,\n" +
            "   SUM(v_destetados) AS v_destetados,\n" +
            "   SUM(v_juveniles) AS v_juveniles,\n" +
            "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
            "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
            "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
            "   SUM(v_otros_sa_perif) AS v_otros_sa_perif,\n" +
            "   SUM(v_otros_sa_cerca) AS v_otros_sa_cerca,\n" +
            "   SUM(v_otros_sa_lejos) AS v_otros_sa_lejos,\n" +
            "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
            "   SUM(m_alfa_otros_sa) AS m_alfa_otros_sa,\n" +
            "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
            "   SUM(m_crias) AS m_crias,\n" +
            "   SUM(m_destetados) AS m_destetados,\n" +
            "   SUM(m_juveniles) AS m_juveniles,\n" +
            "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
            "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
            "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
            "   SUM(m_otros_sa_perif) AS m_otros_sa_perif,\n" +
            "   SUM(m_otros_sa_cerca) AS m_otros_sa_cerca,\n" +
            "   SUM(m_otros_sa_lejos) AS m_otros_sa_lejos,\n" +
            "   date, latitud, longitud, photo_path, comentario \n" +
            "FROM unidsocial\n" +
            "WHERE id_recorrido = :idRecorr;\n")
    fun getSumUnSocByRecorrId(idRecorr: Int): UnidSocial

    @Query("SELECT \n" +
            "   id, id_recorrido,\n" +
            "   pto_observacion, ctx_social, tpo_sustrato ,\n" +
            "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
            "   SUM(v_alfa_otros_sa) AS v_alfa_otros_sa,\n" +
            "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
            "   SUM(v_crias) AS v_crias,\n" +
            "   SUM(v_destetados) AS v_destetados,\n" +
            "   SUM(v_juveniles) AS v_juveniles,\n" +
            "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
            "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
            "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
            "   SUM(v_otros_sa_perif) AS v_otros_sa_perif,\n" +
            "   SUM(v_otros_sa_cerca) AS v_otros_sa_cerca,\n" +
            "   SUM(v_otros_sa_lejos) AS v_otros_sa_lejos,\n" +
            "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
            "   SUM(m_alfa_otros_sa) AS m_alfa_otros_sa,\n" +
            "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
            "   SUM(m_crias) AS m_crias,\n" +
            "   SUM(m_destetados) AS m_destetados,\n" +
            "   SUM(m_juveniles) AS m_juveniles,\n" +
            "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
            "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
            "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
            "   SUM(m_otros_sa_perif) AS m_otros_sa_perif,\n" +
            "   SUM(m_otros_sa_cerca) AS m_otros_sa_cerca,\n" +
            "   SUM(m_otros_sa_lejos) AS m_otros_sa_lejos,\n" +
            "   date, latitud, longitud, photo_path, comentario \n" +
            "FROM \n" +
            "    unidsocial\n" +
            "WHERE \n" +
            "    id_recorrido IN (SELECT id FROM recorrido WHERE id_dia = :idDia);\n")
    fun getSumUnSocByDiaId(idDia: Int): UnidSocial

    @Query("SELECT \n" +
            "   id, id_recorrido,\n" +
            "   pto_observacion, ctx_social, tpo_sustrato ,\n" +
            "   SUM(v_alfa_s4ad) AS v_alfa_s4ad,\n" +
            "   SUM(v_alfa_otros_sa) AS v_alfa_otros_sa,\n" +
            "   SUM(v_hembras_ad) AS v_hembras_ad,\n" +
            "   SUM(v_crias) AS v_crias,\n" +
            "   SUM(v_destetados) AS v_destetados,\n" +
            "   SUM(v_juveniles) AS v_juveniles,\n" +
            "   SUM(v_s4ad_perif) AS v_s4ad_perif,\n" +
            "   SUM(v_s4ad_cerca) AS v_s4ad_cerca,\n" +
            "   SUM(v_s4ad_lejos) AS v_s4ad_lejos,\n" +
            "   SUM(v_otros_sa_perif) AS v_otros_sa_perif,\n" +
            "   SUM(v_otros_sa_cerca) AS v_otros_sa_cerca,\n" +
            "   SUM(v_otros_sa_lejos) AS v_otros_sa_lejos,\n" +
            "   SUM(m_alfa_s4ad) AS m_alfa_s4ad,\n" +
            "   SUM(m_alfa_otros_sa) AS m_alfa_otros_sa,\n" +
            "   SUM(m_hembras_ad) AS m_hembras_ad,\n" +
            "   SUM(m_crias) AS m_crias,\n" +
            "   SUM(m_destetados) AS m_destetados,\n" +
            "   SUM(m_juveniles) AS m_juveniles,\n" +
            "   SUM(m_s4ad_perif) AS m_s4ad_perif,\n" +
            "   SUM(m_s4ad_cerca) AS m_s4ad_cerca,\n" +
            "   SUM(m_s4ad_lejos) AS m_s4ad_lejos,\n" +
            "   SUM(m_otros_sa_perif) AS m_otros_sa_perif,\n" +
            "   SUM(m_otros_sa_cerca) AS m_otros_sa_cerca,\n" +
            "   SUM(m_otros_sa_lejos) AS m_otros_sa_lejos,\n" +
            "   date, latitud, longitud, photo_path, comentario \n" +
            "FROM \n" +
            "    unidsocial\n")
    fun getSumTotal(): UnidSocial
}

