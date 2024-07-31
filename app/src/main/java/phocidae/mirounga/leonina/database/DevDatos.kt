package phocidae.mirounga.leonina.database

import android.content.Context
import phocidae.mirounga.leonina.fragment.DevFragment
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.dao.DiaDAO
import phocidae.mirounga.leonina.dao.RecorrDAO
import phocidae.mirounga.leonina.dao.UsuarioDAO
import phocidae.mirounga.leonina.model.Dia
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.repository.RecorrRepository
import phocidae.mirounga.leonina.repository.UnSocRepository
import phocidae.mirounga.leonina.servicios.GestorUUID
import java.util.UUID

class DevDatos(private val context: Context) {

    fun generarDias(diaDAO: DiaDAO): Array<UUID?> {

        val idCelular = GestorUUID.obtenerAndroidID()
        val diaList = listOf<Dia>(
            Dia(
                celularId = idCelular,
                id = DevFragment.UUID_NULO,
                orden = 0,
                fecha = "2023-10-19"
            ),
            Dia(
                celularId = idCelular,
                id = DevFragment.UUID_NULO,
                orden = 0,
                fecha = "2023-10-20"
            )
        )
        val listUUID = Array<UUID?>(diaList.size) { null }
        diaList.forEachIndexed { idx, dia ->
            listUUID[idx] = diaDAO.insertConUUID(dia)
        }
        return listUUID
    }

    fun generarRecorridos(recorrRepo: RecorrRepository, listDia: Array<UUID>): Array<UUID?> {

        val recorrList = listOf<Recorrido>(
            Recorrido(
                id = DevFragment.UUID_NULO,
                diaId = listDia[0],
                orden = 0,
                observador = "hugo",
                fechaIni = "2023-10-19 12:20:48",
                fechaFin = "2023-10-19 18:07:48",
                latitudIni = -42.081045,
                longitudIni = -63.843673,
                latitudFin = -42.104028,
                longitudFin = -63.735413,
                areaRecorrida = "punta norte",
                meteo = "parcialmente nublado",
                marea = context.getString(R.string.mar_media),
                observaciones = "paramos a comer al mediodia"
            ),
            Recorrido(
                id = DevFragment.UUID_NULO,
                diaId = listDia[0],
                orden = 0,
                observador = "sebastian",
                fechaIni = "2023-10-20 10:15:48",
                fechaFin = "2023-10-20 17:23:48",
                latitudIni = -42.764254,
                longitudIni = -63.633053,
                latitudFin = -42.737037,
                longitudFin = -63.636430,
                areaRecorrida = "punta delgada",
                meteo = "parcialmente nublado",
                marea = context.getString(R.string.mar_bajabaj),
                observaciones = "uno de los censistas avistó un extraterrestre"
            ),
            Recorrido(
                id = DevFragment.UUID_NULO,
                diaId = listDia[1],
                orden = 0,
                observador = "donato",
                fechaIni = "2023-01-21 11:15:48",
                fechaFin = "2023-01-21 15:38:48",
                latitudIni = -43.677592,
                longitudIni = -65.341546,
                latitudFin = -43.639031,
                longitudFin = -65.314234,
                areaRecorrida = "isla escondida",
                meteo = "despejado",
                marea = context.getString(R.string.mar_muyalta),
                observaciones = "es posible saber el grado de compromosio politico de una persona, sabiendo qué tanto consume dulce de leche"
            ),
        )
        val listUUID = Array<UUID?>(recorrList.size) { null }
        recorrList.forEachIndexed { idx, recorr ->
            listUUID[idx] = recorrRepo.insert(context, recorr)
        }
        return listUUID
    }

    fun generarUnidadesSociales(unSocRepo: UnSocRepository, listRecorr: Array<UUID>) {
        val unSocList = listOf(
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[0],
                orden = 0,
                ptoObsUnSoc = context.getString(R.string.pto_alto),
                ctxSocial = context.getString(R.string.ctx_haren),
                tpoSustrato = context.getString(R.string.tpo_arena),
                vAlfaS4Ad = 3,
                vAlfaSams = 4,
                vHembrasAd = 18,
                vCrias = 22,
                vDestetados = 7,
                vJuveniles = 6,
                vS4AdPerif = 3,
                vS4AdCerca = 2,
                vS4AdLejos = 1,
                vOtrosSamsPerif = 2,
                vOtrosSamsCerca = 3,
                vOtrosSamsLejos = 1,
                mAlfaS4Ad = 3,
                mAlfaSams = 4,
                mHembrasAd = 18,
                mCrias = 22,
                mDestetados = 7,
                mJuveniles = 6,
                mS4AdPerif = 3,
                mS4AdCerca = 2,
                mS4AdLejos = 1,
                mOtrosSamsPerif = 2,
                mOtrosSamsCerca = 3,
                mOtrosSamsLejos = 1,
                date = "2023-10-19 13:26:48",
                latitud = -42.074929,
                longitud = -63.819889,
                comentario = "comentario 1"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[0],
                orden = 1,
                ptoObsUnSoc = context.getString(R.string.pto_bajo),
                ctxSocial = context.getString(R.string.ctx_gpoHarenes),
                tpoSustrato = context.getString(R.string.tpo_cRodado),
                vAlfaS4Ad = 2,
                vAlfaSams = 3,
                vHembrasAd = 20,
                vCrias = 25,
                vDestetados = 5,
                vJuveniles = 4,
                vS4AdPerif = 1,
                vS4AdCerca = 2,
                vS4AdLejos = 3,
                vOtrosSamsPerif = 2,
                vOtrosSamsCerca = 1,
                vOtrosSamsLejos = 3,
                mAlfaS4Ad = 2,
                mAlfaSams = 3,
                mHembrasAd = 20,
                mCrias = 25,
                mDestetados = 5,
                mJuveniles = 4,
                mS4AdPerif = 1,
                mS4AdCerca = 2,
                mS4AdLejos = 3,
                mOtrosSamsPerif = 2,
                mOtrosSamsCerca = 1,
                mOtrosSamsLejos = 3,
                date = "2023-10-19 14:26:48",
                latitud = -42.073655,
                longitud = -63.794792,
                comentario = "comentario 2"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[0],
                orden = 2,
                ptoObsUnSoc = context.getString(R.string.pto_alto),
                ctxSocial = context.getString(R.string.ctx_pjaSolitaria),
                tpoSustrato = context.getString(R.string.tpo_mezcla),
                vAlfaS4Ad = 4,
                vAlfaSams = 5,
                vHembrasAd = 28,
                vCrias = 30,
                vDestetados = 6,
                vJuveniles = 7,
                vS4AdPerif = 3,
                vS4AdCerca = 2,
                vS4AdLejos = 4,
                vOtrosSamsPerif = 1,
                vOtrosSamsCerca = 3,
                vOtrosSamsLejos = 2,
                mAlfaS4Ad = 4,
                mAlfaSams = 5,
                mHembrasAd = 28,
                mCrias = 30,
                mDestetados = 6,
                mJuveniles = 7,
                mS4AdPerif = 3,
                mS4AdCerca = 2,
                mS4AdLejos = 4,
                mOtrosSamsPerif = 1,
                mOtrosSamsCerca = 3,
                mOtrosSamsLejos = 2,
                date = "2023-10-19 15:26:48",
                latitud = -42.074164,
                longitud = -63.774866,
                comentario = "comentario 3"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[0],
                orden = 3,
                ptoObsUnSoc = context.getString(R.string.pto_bajo),
                ctxSocial = context.getString(R.string.ctx_indivSolo),
                tpoSustrato = context.getString(R.string.tpo_restinga),
                vAlfaS4Ad = 5,
                vAlfaSams = 6,
                vHembrasAd = 32,
                vCrias = 35,
                vDestetados = 8,
                vJuveniles = 9,
                vS4AdPerif = 4,
                vS4AdCerca = 3,
                vS4AdLejos = 2,
                vOtrosSamsPerif = 1,
                vOtrosSamsCerca = 3,
                vOtrosSamsLejos = 2,
                mAlfaS4Ad = 5,
                mAlfaSams = 6,
                mHembrasAd = 32,
                mCrias = 35,
                mDestetados = 8,
                mJuveniles = 9,
                mS4AdPerif = 4,
                mS4AdCerca = 3,
                mS4AdLejos = 2,
                mOtrosSamsPerif = 1,
                mOtrosSamsCerca = 3,
                mOtrosSamsLejos = 2,
                date = "2023-10-19 18:26:48",
                latitud = -42.081554,
                longitud = -63.757345,
                comentario = "comentario 4"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[1],
                orden = 0,
                ptoObsUnSoc = context.getString(R.string.pto_alto),
                ctxSocial = context.getString(R.string.ctx_haren),
                tpoSustrato = context.getString(R.string.tpo_arena),
                vAlfaS4Ad = 6,
                vAlfaSams = 7,
                vHembrasAd = 35,
                vCrias = 40,
                vDestetados = 9,
                vJuveniles = 10,
                vS4AdPerif = 5,
                vS4AdCerca = 4,
                vS4AdLejos = 3,
                vOtrosSamsPerif = 2,
                vOtrosSamsCerca = 4,
                vOtrosSamsLejos = 1,
                mAlfaS4Ad = 6,
                mAlfaSams = 7,
                mHembrasAd = 35,
                mCrias = 40,
                mDestetados = 9,
                mJuveniles = 10,
                mS4AdPerif = 5,
                mS4AdCerca = 4,
                mS4AdLejos = 3,
                mOtrosSamsPerif = 2,
                mOtrosSamsCerca = 4,
                mOtrosSamsLejos = 1,
                date = "2023-10-20 12:26:48",
                latitud = -42.754045,
                longitud = -63.630907,
                comentario = "comentario 5"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[1],
                orden = 1,
                ptoObsUnSoc = context.getString(R.string.pto_bajo),
                ctxSocial = context.getString(R.string.ctx_gpoHarenes),
                tpoSustrato = context.getString(R.string.tpo_cRodado),
                vAlfaS4Ad = 3,
                vAlfaSams = 4,
                vHembrasAd = 25,
                vCrias = 30,
                vDestetados = 7,
                vJuveniles = 8,
                vS4AdPerif = 2,
                vS4AdCerca = 3,
                vS4AdLejos = 1,
                vOtrosSamsPerif = 1,
                vOtrosSamsCerca = 3,
                vOtrosSamsLejos = 2,
                mAlfaS4Ad = 3,
                mAlfaSams = 4,
                mHembrasAd = 25,
                mCrias = 30,
                mDestetados = 7,
                mJuveniles = 8,
                mS4AdPerif = 2,
                mS4AdCerca = 3,
                mS4AdLejos = 1,
                mOtrosSamsPerif = 1,
                mOtrosSamsCerca = 3,
                mOtrosSamsLejos = 2,
                date = "2023-10-20 11:26:48",
                latitud = -42.748813,
                longitud = -63.634944,
                comentario = "comentario 6"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[1],
                orden = 2,
                ptoObsUnSoc = context.getString(R.string.pto_alto),
                ctxSocial = context.getString(R.string.ctx_pjaSolitaria),
                tpoSustrato = context.getString(R.string.tpo_mezcla),
                vAlfaS4Ad = 4,
                vAlfaSams = 5,
                vHembrasAd = 28,
                vCrias = 32,
                vDestetados = 6,
                vJuveniles = 7,
                vS4AdPerif = 3,
                vS4AdCerca = 2,
                vS4AdLejos = 4,
                vOtrosSamsPerif = 1,
                vOtrosSamsCerca = 2,
                vOtrosSamsLejos = 3,
                mAlfaS4Ad = 4,
                mAlfaSams = 5,
                mHembrasAd = 28,
                mCrias = 32,
                mDestetados = 6,
                mJuveniles = 7,
                mS4AdPerif = 3,
                mS4AdCerca = 2,
                mS4AdLejos = 4,
                mOtrosSamsPerif = 1,
                mOtrosSamsCerca = 2,
                mOtrosSamsLejos = 3,
                date = "2023-10-20 13:26:48",
                latitud = -42.744527,
                longitud = -63.635631,
                comentario = "comentario 7"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[1],
                orden = 3,
                ptoObsUnSoc = context.getString(R.string.pto_bajo),
                ctxSocial = context.getString(R.string.ctx_indivSolo),
                tpoSustrato = context.getString(R.string.tpo_restinga),
                vAlfaS4Ad = 2,
                vAlfaSams = 3,
                vHembrasAd = 18,
                vCrias = 21,
                vDestetados = 4,
                vJuveniles = 5,
                vS4AdPerif = 1,
                vS4AdCerca = 2,
                vS4AdLejos = 3,
                vOtrosSamsPerif = 2,
                vOtrosSamsCerca = 1,
                vOtrosSamsLejos = 3,
                mAlfaS4Ad = 2,
                mAlfaSams = 3,
                mHembrasAd = 18,
                mCrias = 21,
                mDestetados = 4,
                mJuveniles = 5,
                mS4AdPerif = 1,
                mS4AdCerca = 2,
                mS4AdLejos = 3,
                mOtrosSamsPerif = 2,
                mOtrosSamsCerca = 1,
                mOtrosSamsLejos = 3,
                date = "2023-10-20 14:26:48",
                latitud = -42.740619,
                longitud = -63.633140,
                comentario = "comentario 8"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[2],
                orden = 0,
                ptoObsUnSoc = context.getString(R.string.pto_alto),
                ctxSocial = context.getString(R.string.ctx_haren),
                tpoSustrato = context.getString(R.string.tpo_arena),
                vAlfaS4Ad = 7,
                vAlfaSams = 9,
                vHembrasAd = 25,
                vCrias = 30,
                vDestetados = 8,
                vJuveniles = 6,
                vS4AdPerif = 5,
                vS4AdCerca = 4,
                vS4AdLejos = 3,
                vOtrosSamsPerif = 2,
                vOtrosSamsCerca = 1,
                vOtrosSamsLejos = 4,
                mAlfaS4Ad = 7,
                mAlfaSams = 9,
                mHembrasAd = 25,
                mCrias = 30,
                mDestetados = 8,
                mJuveniles = 6,
                mS4AdPerif = 5,
                mS4AdCerca = 4,
                mS4AdLejos = 3,
                mOtrosSamsPerif = 2,
                mOtrosSamsCerca = 1,
                mOtrosSamsLejos = 4,
                date = "2023-10-21 12:26:48",
                latitud = -43.663251,
                longitud = -65.334847,
                comentario = "comentario 9"
            ),
            UnidSocial(
                id = DevFragment.UUID_NULO,
                recorrId = listRecorr[2],
                orden = 1,
                ptoObsUnSoc = context.getString(R.string.pto_bajo),
                ctxSocial = context.getString(R.string.ctx_gpoHarenes),
                tpoSustrato = context.getString(R.string.tpo_cRodado),
                vAlfaS4Ad = 8,
                vAlfaSams = 6,
                vHembrasAd = 18,
                vCrias = 20,
                vDestetados = 7,
                vJuveniles = 4,
                vS4AdPerif = 3,
                vS4AdCerca = 2,
                vS4AdLejos = 1,
                vOtrosSamsPerif = 4,
                vOtrosSamsCerca = 5,
                vOtrosSamsLejos = 6,
                mAlfaS4Ad = 8,
                mAlfaSams = 6,
                mHembrasAd = 18,
                mCrias = 20,
                mDestetados = 7,
                mJuveniles = 4,
                mS4AdPerif = 3,
                mS4AdCerca = 2,
                mS4AdLejos = 1,
                mOtrosSamsPerif = 4,
                mOtrosSamsCerca = 5,
                mOtrosSamsLejos = 6,
                date = "2023-10-21 13:26:48",
                latitud = -43.653316,
                longitud = -65.326059,
                comentario = "comentario 10"
            )
        )
        unSocList.forEach { unsoc ->
            unSocRepo.insert(context, unsoc)
        }
    }

    fun generarUsuario(dao: UsuarioDAO) {
        if (dao.getUsuario("hdonato@donato.com", "hdonato").isEmpty())
            dao.create("hdonato@donato.com", "hdonato", true)
    }

    fun vaciarDias(diaDAO: DiaDAO) {
        diaDAO.deleteAll()
    }

    fun vaciarRecorridos(recorrDAO: RecorrDAO) {
        recorrDAO.deleteAll()
    }
}