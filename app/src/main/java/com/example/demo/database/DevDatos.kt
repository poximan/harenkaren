package com.example.demo.database

import com.example.demo.DevFragment
import com.example.demo.activity.MainActivity
import com.example.demo.dao.DiaDAO
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
import com.example.demo.dao.UsuarioDAO
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import java.util.UUID

class DevDatos {

    fun generarDias(diaDAO: DiaDAO): Array<UUID?> {

        val idUnivoco = MainActivity.obtenerAndroidID()

        val diaList = listOf<Dia>(
            Dia(celularId = idUnivoco, id = DevFragment.UUID_NULO, orden = 0, fecha = "2023/10/19 - 08:20:48"),
            Dia(celularId = idUnivoco, id = DevFragment.UUID_NULO, orden = 0, fecha = "2023/10/20 - 12:17:13")
        )

        val idsRetornos = Array<UUID?>(diaList.size) { null }

        diaList.forEachIndexed { idx, dia ->
            idsRetornos[idx] = diaDAO.insertConUUID(dia)
        }
        return idsRetornos
    }

    fun generarRecorridos(recorrDAO: RecorrDAO, listDia: Array<UUID>) {
        val recorrList = listOf<Recorrido>(
            Recorrido(
                id = 1, diaId = listDia[0], orden = 0, observador = "hugo",
                fechaIni = "2023/10/19 - 12:20:48", fechaFin = "2023/10/19 - 18:07:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -39.555, longitudFin = -61.031,
                areaRecorrida = "punta norte", meteo = "parcialmente nublado", marea = "Media"),
            Recorrido(
                id = 2, diaId = listDia[0], orden = 0, observador = "sebastian",
                fechaIni = "2023/10/19 - 10:15:48", fechaFin = "2023/10/19 - 17:23:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -38.555, longitudFin = -59.031,
                areaRecorrida = "punta delgada", meteo = "parcialmente nublado", marea = "Baja, bajando"),
            Recorrido(
                id = 3, diaId = listDia[1], orden = 0, observador = "donato",
                fechaIni = "2024/01/21 - 11:15:48", fechaFin = "2024/01/21 - 18:38:48",
                latitudIni = -42.123, longitudIni = -62.371, latitudFin = -38.533, longitudFin = -60.311,
                areaRecorrida = "isla escondida", meteo = "despejado", marea = "Muy alta"),
        )

        recorrList.forEach { recorr ->
            recorrDAO.insertConUltInst(recorr)
        }
    }

    fun generarUnidadesSociales(unsocDAO: UnSocDAO) {
        val unSocList = listOf<UnidSocial>(
            UnidSocial(
                id = 1, recorrId = 1, orden = 0, ptoObsUnSoc = "alto", ctxSocial = "haren(H)", tpoSustrato = "arena(A)",
                vAlfaS4Ad = 1, vAlfaSams = 2, vHembrasAd = 10, vCrias = 8, vDestetados = 1,
                vJuveniles = 2, vS4AdPerif = 2, vS4AdCerca = 1, vS4AdLejos = 0,
                vOtrosSamsPerif = 2, vOtrosSamsCerca = 1, vOtrosSamsLejos = 0,
                mAlfaS4Ad = 1, mAlfaSams = 2, mHembrasAd = 10, mCrias = 8, mDestetados = 1,
                mJuveniles = 2, mS4AdPerif = 2, mS4AdCerca = 1, mS4AdLejos = 0,
                mOtrosSamsPerif = 2, mOtrosSamsCerca = 1, mOtrosSamsLejos = 0, date = "2023/10/18 - 17:26:48",
                latitud = -42.079241, longitud = -63.765547, photoPath = "", comentario = "comentario 1"),
            UnidSocial(
                id = 2, recorrId = 1, orden = 0, ptoObsUnSoc = "bajo", ctxSocial = "gpo. harenes(GH)", tpoSustrato = "canto rodado(CR)",
                vAlfaS4Ad = 3, vAlfaSams = 4, vHembrasAd = 15, vCrias = 12, vDestetados = 3,
                vJuveniles = 4, vS4AdPerif = 4, vS4AdCerca = 3, vS4AdLejos = 2,
                vOtrosSamsPerif = 4, vOtrosSamsCerca = 3, vOtrosSamsLejos = 2,
                mAlfaS4Ad = 3, mAlfaSams = 4, mHembrasAd = 15, mCrias = 12, mDestetados = 3,
                mJuveniles = 4, mS4AdPerif = 4, mS4AdCerca = 3, mS4AdLejos = 2,
                mOtrosSamsPerif = 4, mOtrosSamsCerca = 3, mOtrosSamsLejos = 2, date = "2023/10/18 - 14:26:48",
                latitud = -42.083023, longitud = -63.753115, photoPath = "", comentario = "comentario 2"),
            UnidSocial(
                id = 3, recorrId = 1, orden = 0, ptoObsUnSoc = "alto", ctxSocial = "pja. solitaria(PS)", tpoSustrato = "mezcla(M)",
                vAlfaS4Ad = 5, vAlfaSams = 6, vHembrasAd = 20, vCrias = 16, vDestetados = 5,
                vJuveniles = 6, vS4AdPerif = 6, vS4AdCerca = 5, vS4AdLejos = 4,
                vOtrosSamsPerif = 6, vOtrosSamsCerca = 5, vOtrosSamsLejos = 4,
                mAlfaS4Ad = 5, mAlfaSams = 6, mHembrasAd = 20, mCrias = 16, mDestetados = 5,
                mJuveniles = 6, mS4AdPerif = 6, mS4AdCerca = 5, mS4AdLejos = 4,
                mOtrosSamsPerif = 6, mOtrosSamsCerca = 5, mOtrosSamsLejos = 4, date = "2023/10/18 - 18:26:48",
                latitud = -42.096113, longitud = -63.740619, photoPath = "", comentario = "comentario 3"),
            UnidSocial(
                id = 4, recorrId = 1, orden = 0, ptoObsUnSoc = "bajo", ctxSocial = "indiv. solos(S)", tpoSustrato = "restinga(R)",
                vAlfaS4Ad = 7, vAlfaSams = 8, vHembrasAd = 25, vCrias = 20, vDestetados = 7,
                vJuveniles = 8, vS4AdPerif = 8, vS4AdCerca = 7, vS4AdLejos = 6,
                vOtrosSamsPerif = 8, vOtrosSamsCerca = 7, vOtrosSamsLejos = 6,
                mAlfaS4Ad = 7, mAlfaSams = 8, mHembrasAd = 25, mCrias = 20, mDestetados = 7,
                mJuveniles = 8, mS4AdPerif = 8, mS4AdCerca = 7, mS4AdLejos = 6,
                mOtrosSamsPerif = 8, mOtrosSamsCerca = 7, mOtrosSamsLejos = 6, date = "2023/10/18 - 13:26:48",
                latitud = -42.109932, longitud = -63.732213, photoPath = "", comentario = "comentario 4"),
            UnidSocial(
                id = 5, recorrId = 2, orden = 0, ptoObsUnSoc = "alto", ctxSocial = "haren(H)", tpoSustrato = "arena(A)",
                vAlfaS4Ad = 9, vAlfaSams = 10, vHembrasAd = 30, vCrias = 24, vDestetados = 9,
                vJuveniles = 10, vS4AdPerif = 10, vS4AdCerca = 9, vS4AdLejos = 8,
                vOtrosSamsPerif = 10, vOtrosSamsCerca = 9, vOtrosSamsLejos = 8,
                mAlfaS4Ad = 0, mAlfaSams = 0, mHembrasAd = 0, mCrias = 0, mDestetados = 0,
                mJuveniles = 0, mS4AdPerif = 0, mS4AdCerca = 0, mS4AdLejos = 0,
                mOtrosSamsPerif = 0, mOtrosSamsCerca = 0, mOtrosSamsLejos = 0, date = "2023/10/18 - 12:26:48",
                latitud = -42.497655, longitud = -63.607295, photoPath = "", comentario = "comentario 5"),
            UnidSocial(
                id = 6, recorrId = 2, orden = 0, ptoObsUnSoc = "bajo", ctxSocial = "gpo. harenes(GH)", tpoSustrato = "canto rodado(CR)",
                vAlfaS4Ad = 11, vAlfaSams = 12, vHembrasAd = 35, vCrias = 28, vDestetados = 11,
                vJuveniles = 12, vS4AdPerif = 12, vS4AdCerca = 11, vS4AdLejos = 10,
                vOtrosSamsPerif = 12, vOtrosSamsCerca = 11, vOtrosSamsLejos = 10,
                mAlfaS4Ad = 0, mAlfaSams = 0, mHembrasAd = 0, mCrias = 0, mDestetados = 0,
                mJuveniles = 0, mS4AdPerif = 0, mS4AdCerca = 0, mS4AdLejos = 0,
                mOtrosSamsPerif = 0, mOtrosSamsCerca = 0, mOtrosSamsLejos = 0, date = "2023/10/18 - 11:26:48",
                latitud = -42.501262, longitud = -63.607638, photoPath = "", comentario = "comentario 6"),
            UnidSocial(
                id = 7, recorrId = 2, orden = 0, ptoObsUnSoc = "alto", ctxSocial = "pja. solitaria(PS)", tpoSustrato = "mezcla(M)",
                vAlfaS4Ad = 13, vAlfaSams = 14, vHembrasAd = 40, vCrias = 32, vDestetados = 13,
                vJuveniles = 14, vS4AdPerif = 14, vS4AdCerca = 13, vS4AdLejos = 12,
                vOtrosSamsPerif = 14, vOtrosSamsCerca = 13, vOtrosSamsLejos = 12,
                mAlfaS4Ad = 0, mAlfaSams = 0, mHembrasAd = 0, mCrias = 0, mDestetados = 0,
                mJuveniles = 0, mS4AdPerif = 0, mS4AdCerca = 0, mS4AdLejos = 0,
                mOtrosSamsPerif = 0, mOtrosSamsCerca = 0, mOtrosSamsLejos = 0, date = "2023/10/18 - 09:26:48",
                latitud = -42.506135, longitud = -63.603645, photoPath = "", comentario = "comentario 7"),
            UnidSocial(
                id = 8, recorrId = 3, orden = 0, ptoObsUnSoc = "bajo", ctxSocial = "indiv. solos(S)", tpoSustrato = "restinga(R)",
                vAlfaS4Ad = 0, vAlfaSams = 0, vHembrasAd = 0, vCrias = 0, vDestetados = 0,
                vJuveniles = 0, vS4AdPerif = 0, vS4AdCerca = 0, vS4AdLejos = 0,
                vOtrosSamsPerif = 0, vOtrosSamsCerca = 0, vOtrosSamsLejos = 0,
                mAlfaS4Ad = 15, mAlfaSams = 16, mHembrasAd = 45, mCrias = 36, mDestetados = 15,
                mJuveniles = 16, mS4AdPerif = 16, mS4AdCerca = 15, mS4AdLejos = 14,
                mOtrosSamsPerif = 16, mOtrosSamsCerca = 15, mOtrosSamsLejos = 14, date = "2023/10/19 - 15:26:48",
                latitud = -43.664679, longitud = -65.336265, photoPath = "", comentario = "comentario 8"),
            UnidSocial(
                id = 9, recorrId = 3, orden = 0, ptoObsUnSoc = "alto", ctxSocial = "haren(H)", tpoSustrato = "arena(A)",
                vAlfaS4Ad = 0, vAlfaSams = 0, vHembrasAd = 0, vCrias = 0, vDestetados = 0,
                vJuveniles = 0, vS4AdPerif = 0, vS4AdCerca = 0, vS4AdLejos = 0,
                vOtrosSamsPerif = 0, vOtrosSamsCerca = 0, vOtrosSamsLejos = 0,
                mAlfaS4Ad = 17, mAlfaSams = 18, mHembrasAd = 50, mCrias = 40, mDestetados = 17,
                mJuveniles = 18, mS4AdPerif = 18, mS4AdCerca = 17, mS4AdLejos = 16,
                mOtrosSamsPerif = 18, mOtrosSamsCerca = 17, mOtrosSamsLejos = 16, date = "2023/10/19 - 16:26:48",
                latitud = -43.657476, longitud = -65.332543, photoPath = "", comentario = "comentario 9"),
            UnidSocial(
                id = 10, recorrId = 3, orden = 0, ptoObsUnSoc = "bajo", ctxSocial = "gpo. harenes(GH)", tpoSustrato = "canto rodado(CR)",
                vAlfaS4Ad = 0, vAlfaSams = 0, vHembrasAd = 0, vCrias = 0, vDestetados = 0,
                vJuveniles = 0, vS4AdPerif = 0, vS4AdCerca = 0, vS4AdLejos = 0,
                vOtrosSamsPerif = 0, vOtrosSamsCerca = 0, vOtrosSamsLejos = 0,
                mAlfaS4Ad = 19, mAlfaSams = 20, mHembrasAd = 55, mCrias = 44, mDestetados = 19,
                mJuveniles = 20, mS4AdPerif = 20, mS4AdCerca = 19, mS4AdLejos = 18,
                mOtrosSamsPerif = 20, mOtrosSamsCerca = 19, mOtrosSamsLejos = 18, date = "2023/10/19 - 13:26:48",
                latitud = -43.652570, longitud = -65.325500, photoPath = "", comentario = "comentario 10")
        )

        unSocList.forEach { unsoc ->
            unsocDAO.insertConUltInst(unsoc)
        }
    }

    fun generarUsuario(dao: UsuarioDAO) {
        if(dao.getUsuario("hdonato@donato.com","hdonato").isEmpty())
            dao.create("hdonato@donato.com","hdonato",true)
    }

    fun vaciarDias(diaDAO: DiaDAO) {
        diaDAO.deleteAll()
    }

    fun vaciarRecorridos(recorrDAO: RecorrDAO) {
        recorrDAO.deleteAll()
    }

    fun vaciarUnidadesSociales(unsocDAO: UnSocDAO) {
        unsocDAO.deleteAll()
    }
}