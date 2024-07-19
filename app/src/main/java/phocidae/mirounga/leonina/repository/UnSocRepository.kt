package phocidae.mirounga.leonina.repository

import android.content.Context
import phocidae.mirounga.leonina.dao.UnSocDAO
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.servicios.IdiomaAdapter
import java.util.UUID

class UnSocRepository(private val dao: UnSocDAO) {

    fun insert(context: Context, unidSocial: UnidSocial): UUID {
        val idiomasaurio = IdiomaAdapter()
        val unidSocialAdaptado = idiomasaurio.persistenciaUnSoc(context, unidSocial)
        return dao.insertConUUID(unidSocialAdaptado)
    }

    fun update(unidSocial: UnidSocial) {
        dao.update(unidSocial)
    }

    fun readUnico(id: UUID): UnidSocial {
        return dao.getUnSocByUUID(id)
    }

    fun readConFK(idRecorr: UUID, context: Context): List<UnidSocial> {
        val idiomasaurio = IdiomaAdapter()
        val listaIntermedia = dao.getUnSocByRecorrId(idRecorr)

        val listaAdaptada = listaIntermedia.map { elem ->
            idiomasaurio.viewModelUnSoc(context, elem)
        }
        return listaAdaptada
    }

    fun getFechaObservada(idRecorr: UUID): String {
        return dao.getFechaObservada(idRecorr)
    }

    /*
    SQL para graficar
     */
    fun readSumUnSocByDiaId(id: UUID): UnidSocial? {
        return try {
            dao.getTotalByDiaId(id)
        } catch (e: NullPointerException) {
            null
        }
    }

    /*
    SQL para graficar
     */
    fun readSumUnSocByRecorrId(id: UUID): UnidSocial? {
        return try {
            dao.getTotalByRecorrId(id)
        } catch (e: NullPointerException) {
            null
        }
    }

    fun readSumTotal(): UnidSocial {
        return dao.getSumTotal()
    }

    fun readAsynConFK(idRecorr: UUID): List<UnidSocial> {
        return dao.getUnSocByRecorrId(idRecorr)
    }
}

