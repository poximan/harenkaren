package com.example.demo.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.demo.dao.RecorrDAO
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import com.example.demo.servicios.GestorUUID
import com.example.demo.servicios.IdiomaAdapter
import java.util.UUID

class RecorrRepository(private val dao: RecorrDAO) {

    companion object {
        const val EXTREMOS_GPS = "extremo"
    }

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

    fun getAllPorAnio(anio: String, unSocList: List<UnidSocial>): List<UnidSocial> {

        val unSocMutante = unSocList.toMutableList()
        val uuidgenerico = GestorUUID.obtenerUUID()
        val recorrExtremos = dao.getAllPorAnio(anio)

        for(punto in recorrExtremos){

            val unidInicio = UnidSocial(uuidgenerico, punto.id,"")
            unidInicio.latitud = punto.latitudIni
            unidInicio.longitud = punto.longitudIni
            unidInicio.comentario = EXTREMOS_GPS
            unSocMutante.add(0, unidInicio)

            val unidFin = UnidSocial(uuidgenerico, punto.id,"")
            unidFin.latitud = punto.latitudFin
            unidFin.longitud = punto.longitudFin
            unidFin.comentario = EXTREMOS_GPS
            unSocMutante.add(unidFin)
        }
        return unSocMutante
    }
}