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
            "   SUM(alfa_s4ad) AS alfa_s4ad,\n" +
            "   SUM(alfa_otros_sa) AS alfa_otros_sa,\n" +
            "   SUM(hembras_ad) AS hembras_ad,\n" +
            "   SUM(crias_vivas) AS crias_vivas,\n" +
            "   SUM(crias_muertas) AS crias_muertas,\n" +
            "   SUM(destetados) AS destetados,\n" +
            "   SUM(juveniles) AS juveniles,\n" +
            "   SUM(s4ad_perif) AS s4ad_perif,\n" +
            "   SUM(s4ad_cerca) AS s4ad_cerca,\n" +
            "   SUM(s4ad_lejos) AS s4ad_lejos,\n" +
            "   SUM(otros_sa_perif) AS otros_sa_perif,\n" +
            "   SUM(otros_sa_cerca) AS otros_sa_cerca,\n" +
            "   SUM(otros_sa_lejos) AS otros_sa_lejos,\n" +
            "   date, latitude, longitude, photo_path, comentario \n" +
            "FROM unidsocial\n" +
            "WHERE id_recorrido = :idRecorr;\n")
    fun getSumUnSocByRecorrId(idRecorr: Int): UnidSocial

    @Query("SELECT \n" +
            "   id, id_recorrido,\n" +
            "   pto_observacion, ctx_social, tpo_sustrato ,\n" +
            "   SUM(alfa_s4ad) AS alfa_s4ad,\n" +
            "   SUM(alfa_otros_sa) AS alfa_otros_sa,\n" +
            "   SUM(hembras_ad) AS hembras_ad,\n" +
            "   SUM(crias_vivas) AS crias_vivas,\n" +
            "   SUM(crias_muertas) AS crias_muertas,\n" +
            "   SUM(destetados) AS destetados,\n" +
            "   SUM(juveniles) AS juveniles,\n" +
            "   SUM(s4ad_perif) AS s4ad_perif,\n" +
            "   SUM(s4ad_cerca) AS s4ad_cerca,\n" +
            "   SUM(s4ad_lejos) AS s4ad_lejos,\n" +
            "   SUM(otros_sa_perif) AS otros_sa_perif,\n" +
            "   SUM(otros_sa_cerca) AS otros_sa_cerca,\n" +
            "   SUM(otros_sa_lejos) AS otros_sa_lejos,\n" +
            "   date, latitude, longitude, photo_path, comentario \n" +
            "FROM \n" +
            "    unidsocial\n" +
            "WHERE \n" +
            "    id_recorrido IN (SELECT id FROM recorrido WHERE id_dia = :idDia);\n")
    fun getSumUnSocByDiaId(idDia: Int): UnidSocial

    @Query("SELECT \n" +
            "   id, id_recorrido,\n" +
            "   pto_observacion, ctx_social, tpo_sustrato ,\n" +
            "   SUM(alfa_s4ad) AS alfa_s4ad,\n" +
            "   SUM(alfa_otros_sa) AS alfa_otros_sa,\n" +
            "   SUM(hembras_ad) AS hembras_ad,\n" +
            "   SUM(crias_vivas) AS crias_vivas,\n" +
            "   SUM(crias_muertas) AS crias_muertas,\n" +
            "   SUM(destetados) AS destetados,\n" +
            "   SUM(juveniles) AS juveniles,\n" +
            "   SUM(s4ad_perif) AS s4ad_perif,\n" +
            "   SUM(s4ad_cerca) AS s4ad_cerca,\n" +
            "   SUM(s4ad_lejos) AS s4ad_lejos,\n" +
            "   SUM(otros_sa_perif) AS otros_sa_perif,\n" +
            "   SUM(otros_sa_cerca) AS otros_sa_cerca,\n" +
            "   SUM(otros_sa_lejos) AS otros_sa_lejos,\n" +
            "   date, latitude, longitude, photo_path, comentario \n" +
            "FROM \n" +
            "    unidsocial\n")
    fun getSumTotal(): UnidSocial
}

