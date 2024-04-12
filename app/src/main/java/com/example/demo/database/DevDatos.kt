package com.example.demo.database

import android.content.Context
import android.provider.Settings
import com.example.demo.dao.DiaDAO
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
import com.example.demo.dao.UsuarioDAO
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial

class DevDatos {

    private fun obtenerAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun generarDias(diaDAO: DiaDAO, context: Context) {

        val idUnivoco = obtenerAndroidID(context)

        val diaList = listOf<Dia>(
            Dia(celularId = idUnivoco, id = 1, fecha = "2023/10/19 - 08:20:48", meteo = "parcialmente nublado"),
            Dia(celularId = idUnivoco, id = 2, fecha = "2023/10/20 - 12:17:13", meteo = "despejado")
        )

        diaList.forEach { dia ->
            diaDAO.insert(dia)
        }
    }

    fun generarRecorridos(recorrDAO: RecorrDAO) {
        val recorrList = listOf<Recorrido>(
            Recorrido(
                id = 1, diaId = 1, observador = "hugo",
                fechaIni = "2023/10/19 - 12:20:48", fechaFin = "2023/10/19 - 18:07:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -39.555, longitudFin = -61.031,
                areaRecorrida = "punta norte"),
            Recorrido(
                id = 2, diaId = 1, observador = "sebastian",
                fechaIni = "2023/10/19 - 10:15:48", fechaFin = "2023/10/19 - 17:23:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -38.555, longitudFin = -59.031,
                areaRecorrida = "punta delgada"),
            Recorrido(
                id = 3, diaId = 2, observador = "donato",
                fechaIni = "2024/01/21 - 11:15:48", fechaFin = "2024/01/21 - 18:38:48",
                latitudIni = -42.123, longitudIni = -62.371, latitudFin = -38.533, longitudFin = -60.311,
                areaRecorrida = "isla escondida"),
        )

        recorrList.forEach { recorr ->
            recorrDAO.insert(recorr)
        }
    }

    fun generarUnidadesSociales(unsocDAO: UnSocDAO) {
        val unSocList = listOf<UnidSocial>(
            UnidSocial(
                id = 1, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 1, vAlfaSams = 2, vHembrasAd = 10, vCrias = 8, vDestetados = 1,
                vJuveniles = 2, vS4AdPerif = 2, vS4AdCerca = 1, vS4AdLejos = 0,
                vOtrosSamsPerif = 2, vOtrosSamsCerca = 1, vOtrosSamsLejos = 0,
                mAlfaS4Ad = 1, mAlfaSams = 2, mHembrasAd = 10, mCrias = 8, mDestetados = 1,
                mJuveniles = 2, mS4AdPerif = 2, mS4AdCerca = 1, mS4AdLejos = 0,
                mOtrosSamsPerif = 2, mOtrosSamsCerca = 1, mOtrosSamsLejos = 0, date = "2023/10/18 - 17:26:48",
                latitud = -42.079241, longitud = -63.765547, photoPath = "", comentario = "comentario 1"),
            UnidSocial(
                id = 2, recorrId = 1, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 3, vAlfaSams = 4, vHembrasAd = 15, vCrias = 12, vDestetados = 3,
                vJuveniles = 4, vS4AdPerif = 4, vS4AdCerca = 3, vS4AdLejos = 2,
                vOtrosSamsPerif = 4, vOtrosSamsCerca = 3, vOtrosSamsLejos = 2,
                mAlfaS4Ad = 3, mAlfaSams = 4, mHembrasAd = 15, mCrias = 12, mDestetados = 3,
                mJuveniles = 4, mS4AdPerif = 4, mS4AdCerca = 3, mS4AdLejos = 2,
                mOtrosSamsPerif = 4, mOtrosSamsCerca = 3, mOtrosSamsLejos = 2, date = "2023/10/18 - 14:26:48",
                latitud = -42.083023, longitud = -63.753115, photoPath = "", comentario = "comentario 2"),
            UnidSocial(
                id = 3, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "PS", tpoSustrato = "M",
                vAlfaS4Ad = 5, vAlfaSams = 6, vHembrasAd = 20, vCrias = 16, vDestetados = 5,
                vJuveniles = 6, vS4AdPerif = 6, vS4AdCerca = 5, vS4AdLejos = 4,
                vOtrosSamsPerif = 6, vOtrosSamsCerca = 5, vOtrosSamsLejos = 4,
                mAlfaS4Ad = 5, mAlfaSams = 6, mHembrasAd = 20, mCrias = 16, mDestetados = 5,
                mJuveniles = 6, mS4AdPerif = 6, mS4AdCerca = 5, mS4AdLejos = 4,
                mOtrosSamsPerif = 6, mOtrosSamsCerca = 5, mOtrosSamsLejos = 4, date = "2023/10/18 - 18:26:48",
                latitud = -42.096113, longitud = -63.740619, photoPath = "", comentario = "comentario 3"),
            UnidSocial(
                id = 4, recorrId = 1, ptoObsUnSoc = "bajo", ctxSocial = "S", tpoSustrato = "R",
                vAlfaS4Ad = 7, vAlfaSams = 8, vHembrasAd = 25, vCrias = 20, vDestetados = 7,
                vJuveniles = 8, vS4AdPerif = 8, vS4AdCerca = 7, vS4AdLejos = 6,
                vOtrosSamsPerif = 8, vOtrosSamsCerca = 7, vOtrosSamsLejos = 6,
                mAlfaS4Ad = 7, mAlfaSams = 8, mHembrasAd = 25, mCrias = 20, mDestetados = 7,
                mJuveniles = 8, mS4AdPerif = 8, mS4AdCerca = 7, mS4AdLejos = 6,
                mOtrosSamsPerif = 8, mOtrosSamsCerca = 7, mOtrosSamsLejos = 6, date = "2023/10/18 - 13:26:48",
                latitud = -42.109932, longitud = -63.732213, photoPath = "", comentario = "comentario 4"),
            UnidSocial(
                id = 5, recorrId = 2, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 9, vAlfaSams = 10, vHembrasAd = 30, vCrias = 24, vDestetados = 9,
                vJuveniles = 10, vS4AdPerif = 10, vS4AdCerca = 9, vS4AdLejos = 8,
                vOtrosSamsPerif = 10, vOtrosSamsCerca = 9, vOtrosSamsLejos = 8,
                mAlfaS4Ad = 9, mAlfaSams = 10, mHembrasAd = 30, mCrias = 24, mDestetados = 9,
                mJuveniles = 10, mS4AdPerif = 10, mS4AdCerca = 9, mS4AdLejos = 8,
                mOtrosSamsPerif = 10, mOtrosSamsCerca = 9, mOtrosSamsLejos = 8, date = "2023/10/18 - 12:26:48",
                latitud = -42.497655, longitud = -63.607295, photoPath = "", comentario = "comentario 5"),
            UnidSocial(
                id = 6, recorrId = 2, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 11, vAlfaSams = 12, vHembrasAd = 35, vCrias = 28, vDestetados = 11,
                vJuveniles = 12, vS4AdPerif = 12, vS4AdCerca = 11, vS4AdLejos = 10,
                vOtrosSamsPerif = 12, vOtrosSamsCerca = 11, vOtrosSamsLejos = 10,
                mAlfaS4Ad = 11, mAlfaSams = 12, mHembrasAd = 35, mCrias = 28, mDestetados = 11,
                mJuveniles = 12, mS4AdPerif = 12, mS4AdCerca = 11, mS4AdLejos = 10,
                mOtrosSamsPerif = 12, mOtrosSamsCerca = 11, mOtrosSamsLejos = 10, date = "2023/10/18 - 11:26:48",
                latitud = -42.501262, longitud = -63.607638, photoPath = "", comentario = "comentario 6"),
            UnidSocial(
                id = 7, recorrId = 2, ptoObsUnSoc = "alto", ctxSocial = "PS", tpoSustrato = "M",
                vAlfaS4Ad = 13, vAlfaSams = 14, vHembrasAd = 40, vCrias = 32, vDestetados = 13,
                vJuveniles = 14, vS4AdPerif = 14, vS4AdCerca = 13, vS4AdLejos = 12,
                vOtrosSamsPerif = 14, vOtrosSamsCerca = 13, vOtrosSamsLejos = 12,
                mAlfaS4Ad = 13, mAlfaSams = 14, mHembrasAd = 40, mCrias = 32, mDestetados = 13,
                mJuveniles = 14, mS4AdPerif = 14, mS4AdCerca = 13, mS4AdLejos = 12,
                mOtrosSamsPerif = 14, mOtrosSamsCerca = 13, mOtrosSamsLejos = 12, date = "2023/10/18 - 09:26:48",
                latitud = -42.506135, longitud = -63.603645, photoPath = "", comentario = "comentario 7"),
            UnidSocial(
                id = 8, recorrId = 3, ptoObsUnSoc = "bajo", ctxSocial = "S", tpoSustrato = "R",
                vAlfaS4Ad = 15, vAlfaSams = 16, vHembrasAd = 45, vCrias = 36, vDestetados = 15,
                vJuveniles = 16, vS4AdPerif = 16, vS4AdCerca = 15, vS4AdLejos = 14,
                vOtrosSamsPerif = 16, vOtrosSamsCerca = 15, vOtrosSamsLejos = 14,
                mAlfaS4Ad = 15, mAlfaSams = 16, mHembrasAd = 45, mCrias = 36, mDestetados = 15,
                mJuveniles = 16, mS4AdPerif = 16, mS4AdCerca = 15, mS4AdLejos = 14,
                mOtrosSamsPerif = 16, mOtrosSamsCerca = 15, mOtrosSamsLejos = 14, date = "2023/10/19 - 15:26:48",
                latitud = -43.664679, longitud = -65.336265, photoPath = "", comentario = "comentario 8"),
            UnidSocial(
                id = 9, recorrId = 3, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 17, vAlfaSams = 18, vHembrasAd = 50, vCrias = 40, vDestetados = 17,
                vJuveniles = 18, vS4AdPerif = 18, vS4AdCerca = 17, vS4AdLejos = 16,
                vOtrosSamsPerif = 18, vOtrosSamsCerca = 17, vOtrosSamsLejos = 16,
                mAlfaS4Ad = 17, mAlfaSams = 18, mHembrasAd = 50, mCrias = 40, mDestetados = 17,
                mJuveniles = 18, mS4AdPerif = 18, mS4AdCerca = 17, mS4AdLejos = 16,
                mOtrosSamsPerif = 18, mOtrosSamsCerca = 17, mOtrosSamsLejos = 16, date = "2023/10/19 - 16:26:48",
                latitud = -43.657476, longitud = -65.332543, photoPath = "", comentario = "comentario 9"),
            UnidSocial(
                id = 10, recorrId = 3, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 19, vAlfaSams = 20, vHembrasAd = 55, vCrias = 44, vDestetados = 19,
                vJuveniles = 20, vS4AdPerif = 20, vS4AdCerca = 19, vS4AdLejos = 18,
                vOtrosSamsPerif = 20, vOtrosSamsCerca = 19, vOtrosSamsLejos = 18,
                mAlfaS4Ad = 19, mAlfaSams = 20, mHembrasAd = 55, mCrias = 44, mDestetados = 19,
                mJuveniles = 20, mS4AdPerif = 20, mS4AdCerca = 19, mS4AdLejos = 18,
                mOtrosSamsPerif = 20, mOtrosSamsCerca = 19, mOtrosSamsLejos = 18, date = "2023/10/19 - 13:26:48",
                latitud = -43.652570, longitud = -65.325500, photoPath = "", comentario = "comentario 10")
        )

        unSocList.forEach { unsoc ->
            unsocDAO.insert(unsoc)
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