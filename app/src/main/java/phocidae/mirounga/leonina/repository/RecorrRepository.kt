package phocidae.mirounga.leonina.repository

import android.content.Context
import androidx.lifecycle.LiveData
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.dao.RecorrDAO
import phocidae.mirounga.leonina.dao.UnSocDAO
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.servicios.GestorUUID
import phocidae.mirounga.leonina.servicios.IdiomaAdapter
import java.util.UUID

class RecorrRepository(private val dao: RecorrDAO) {

    val recorrListAll: LiveData<List<Recorrido>> = dao.getAll()

    fun insert(context: Context, recorrido: Recorrido): UUID {
        val idiomasaurio = IdiomaAdapter()
        val recorrAdaptado = idiomasaurio.persistenciaRecorr(context, recorrido)
        return dao.insertConUUID(recorrAdaptado)
    }

    fun update(recorrido: Recorrido) {
        dao.update(recorrido)
    }

    fun readUnico(id: UUID): Recorrido {
        return dao.getRecorrByUUID(id)
    }

    fun readConFK(id: UUID, context: Context): List<Recorrido> {
        val idiomasaurio = IdiomaAdapter()
        val listaIntermedia = dao.getRecorrByDiaId(id)

        val listaAdaptada = listaIntermedia.map { elem ->
            idiomasaurio.viewModelRecorr(context, elem)
        }
        return listaAdaptada
    }

    fun readAsynConFK(id: UUID): List<Recorrido> {
        return dao.getRecorrByDiaId(id)
    }

    fun getFechaObservada(idDia: UUID): String {
        return dao.getFechaObservada(idDia)
    }

    fun getAllPorAnio(desde: String, hasta: String, unSocDAO: UnSocDAO, contexto: Context): List<UnidSocial> {

        val unSocMutante = mutableListOf<UnidSocial>()
        val uuidgenerico = GestorUUID.obtenerUUID()
        val recorrList = dao.getEntreFechas(desde, hasta)

        for (punto in recorrList) {

            val unSocList =
                unSocDAO.getEntreFechas(desde, hasta)
                    .sortedWith(compareBy({ it.recorrId }, { it.orden }))

            if (unSocList.isEmpty())
                return unSocMutante

            val unidInicio = UnidSocial(uuidgenerico, punto.id, "")
            unidInicio.latitud = punto.latitudIni
            unidInicio.longitud = punto.longitudIni
            unidInicio.comentario = contexto.getString(R.string.osm_extini)

            val unidFin = UnidSocial(uuidgenerico, punto.id, "")
            unidFin.latitud = punto.latitudFin
            unidFin.longitud = punto.longitudFin
            unidFin.comentario = contexto.getString(R.string.osm_extfin)

            unSocMutante.add(unidInicio)
            unSocMutante.addAll(unSocList)
            unSocMutante.add(unidFin)
        }
        return unSocMutante
    }
}