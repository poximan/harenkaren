package com.example.demo.database

import com.example.demo.dao.DiaDAO
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
import com.example.demo.dao.UsuarioDAO
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial

class DevDatos {

    fun generarDias(diaDAO: DiaDAO) {
        val diaList = listOf<Dia>(
            Dia(id = 1, fecha = "2023/10/19 - 08:20:48", meteo = "parcialmente nublado"),
            Dia(id = 2, fecha = "2023/10/20 - 12:17:13", meteo = "despejado")
        )

        diaList.forEach { dia ->
            diaDAO.insert(dia)
        }
    }

    fun generarRecorridos(recorrDAO: RecorrDAO) {
        val recorrList = listOf<Recorrido>(
            Recorrido(
                id = 1, diaId = 1, observador = "hugo",
                fechaIni = "2023/10/19 - 08:20:48", fechaFin = "2023/10/19 - 18:07:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -39.555, longitudFin = -61.031,
                areaRecorrida = "norte-sur"),
            Recorrido(
                id = 2, diaId = 1, observador = "sebastian",
                fechaIni = "2023/10/19 - 08:15:48", fechaFin = "2023/10/19 - 15:23:48",
                latitudIni = -42.555, longitudIni = -65.031, latitudFin = -38.555, longitudFin = -59.031,
                areaRecorrida = "suroeste-noroeste"),
            Recorrido(
                id = 3, diaId = 2, observador = "donato",
                fechaIni = "2023/10/21 - 16:15:48", fechaFin = "2023/10/19 - 14:38:48",
                latitudIni = -42.000, longitudIni = -62.371, latitudFin = -38.533, longitudFin = -60.311,
                areaRecorrida = "este-oeste"),
        )

        recorrList.forEach { recorr ->
            recorrDAO.insert(recorr)
        }
    }

    fun generarUnidadesSociales(unsocDAO: UnSocDAO) {
        val unSocList = listOf<UnidSocial>(
            UnidSocial(
                id = 1, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 1, vAlfaOtrosSA = 2, vHembrasAd = 10, vCrias = 8, vDestetados = 1,
                vJuveniles = 2, vS4AdPerif = 2, vS4AdCerca = 1, vS4AdLejos = 0,
                vOtrosSAPerif = 2, vOtrosSACerca = 1, vOtrosSALejos = 0,
                mAlfaS4Ad = 1, mAlfaOtrosSA = 2, mHembrasAd = 10, mCrias = 8, mDestetados = 1,
                mJuveniles = 2, mS4AdPerif = 2, mS4AdCerca = 1, mS4AdLejos = 0,
                mOtrosSAPerif = 2, mOtrosSACerca = 1, mOtrosSALejos = 0, date = "2023/10/18 - 17:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 1"),
            UnidSocial(
                id = 2, recorrId = 1, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 3, vAlfaOtrosSA = 4, vHembrasAd = 15, vCrias = 12, vDestetados = 3,
                vJuveniles = 4, vS4AdPerif = 4, vS4AdCerca = 3, vS4AdLejos = 2,
                vOtrosSAPerif = 4, vOtrosSACerca = 3, vOtrosSALejos = 2,
                mAlfaS4Ad = 3, mAlfaOtrosSA = 4, mHembrasAd = 15, mCrias = 12, mDestetados = 3,
                mJuveniles = 4, mS4AdPerif = 4, mS4AdCerca = 3, mS4AdLejos = 2,
                mOtrosSAPerif = 4, mOtrosSACerca = 3, mOtrosSALejos = 2, date = "2023/10/18 - 14:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 2"),
            UnidSocial(
                id = 3, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "PS", tpoSustrato = "M",
                vAlfaS4Ad = 5, vAlfaOtrosSA = 6, vHembrasAd = 20, vCrias = 16, vDestetados = 5,
                vJuveniles = 6, vS4AdPerif = 6, vS4AdCerca = 5, vS4AdLejos = 4,
                vOtrosSAPerif = 6, vOtrosSACerca = 5, vOtrosSALejos = 4,
                mAlfaS4Ad = 5, mAlfaOtrosSA = 6, mHembrasAd = 20, mCrias = 16, mDestetados = 5,
                mJuveniles = 6, mS4AdPerif = 6, mS4AdCerca = 5, mS4AdLejos = 4,
                mOtrosSAPerif = 6, mOtrosSACerca = 5, mOtrosSALejos = 4, date = "2023/10/18 - 18:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 3"),
            UnidSocial(
                id = 4, recorrId = 1, ptoObsUnSoc = "bajo", ctxSocial = "S", tpoSustrato = "R",
                vAlfaS4Ad = 7, vAlfaOtrosSA = 8, vHembrasAd = 25, vCrias = 20, vDestetados = 7,
                vJuveniles = 8, vS4AdPerif = 8, vS4AdCerca = 7, vS4AdLejos = 6,
                vOtrosSAPerif = 8, vOtrosSACerca = 7, vOtrosSALejos = 6,
                mAlfaS4Ad = 7, mAlfaOtrosSA = 8, mHembrasAd = 25, mCrias = 20, mDestetados = 7,
                mJuveniles = 8, mS4AdPerif = 8, mS4AdCerca = 7, mS4AdLejos = 6,
                mOtrosSAPerif = 8, mOtrosSACerca = 7, mOtrosSALejos = 6, date = "2023/10/18 - 13:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 4"),
            UnidSocial(
                id = 5, recorrId = 2, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 9, vAlfaOtrosSA = 10, vHembrasAd = 30, vCrias = 24, vDestetados = 9,
                vJuveniles = 10, vS4AdPerif = 10, vS4AdCerca = 9, vS4AdLejos = 8,
                vOtrosSAPerif = 10, vOtrosSACerca = 9, vOtrosSALejos = 8,
                mAlfaS4Ad = 9, mAlfaOtrosSA = 10, mHembrasAd = 30, mCrias = 24, mDestetados = 9,
                mJuveniles = 10, mS4AdPerif = 10, mS4AdCerca = 9, mS4AdLejos = 8,
                mOtrosSAPerif = 10, mOtrosSACerca = 9, mOtrosSALejos = 8, date = "2023/10/18 - 12:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 5"),
            UnidSocial(
                id = 6, recorrId = 2, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 11, vAlfaOtrosSA = 12, vHembrasAd = 35, vCrias = 28, vDestetados = 11,
                vJuveniles = 12, vS4AdPerif = 12, vS4AdCerca = 11, vS4AdLejos = 10,
                vOtrosSAPerif = 12, vOtrosSACerca = 11, vOtrosSALejos = 10,
                mAlfaS4Ad = 11, mAlfaOtrosSA = 12, mHembrasAd = 35, mCrias = 28, mDestetados = 11,
                mJuveniles = 12, mS4AdPerif = 12, mS4AdCerca = 11, mS4AdLejos = 10,
                mOtrosSAPerif = 12, mOtrosSACerca = 11, mOtrosSALejos = 10, date = "2023/10/18 - 11:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 6"),
            UnidSocial(
                id = 7, recorrId = 2, ptoObsUnSoc = "alto", ctxSocial = "PS", tpoSustrato = "M",
                vAlfaS4Ad = 13, vAlfaOtrosSA = 14, vHembrasAd = 40, vCrias = 32, vDestetados = 13,
                vJuveniles = 14, vS4AdPerif = 14, vS4AdCerca = 13, vS4AdLejos = 12,
                vOtrosSAPerif = 14, vOtrosSACerca = 13, vOtrosSALejos = 12,
                mAlfaS4Ad = 13, mAlfaOtrosSA = 14, mHembrasAd = 40, mCrias = 32, mDestetados = 13,
                mJuveniles = 14, mS4AdPerif = 14, mS4AdCerca = 13, mS4AdLejos = 12,
                mOtrosSAPerif = 14, mOtrosSACerca = 13, mOtrosSALejos = 12, date = "2023/10/18 - 09:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 7"),
            UnidSocial(
                id = 8, recorrId = 3, ptoObsUnSoc = "bajo", ctxSocial = "S", tpoSustrato = "R",
                vAlfaS4Ad = 15, vAlfaOtrosSA = 16, vHembrasAd = 45, vCrias = 36, vDestetados = 15,
                vJuveniles = 16, vS4AdPerif = 16, vS4AdCerca = 15, vS4AdLejos = 14,
                vOtrosSAPerif = 16, vOtrosSACerca = 15, vOtrosSALejos = 14,
                mAlfaS4Ad = 15, mAlfaOtrosSA = 16, mHembrasAd = 45, mCrias = 36, mDestetados = 15,
                mJuveniles = 16, mS4AdPerif = 16, mS4AdCerca = 15, mS4AdLejos = 14,
                mOtrosSAPerif = 16, mOtrosSACerca = 15, mOtrosSALejos = 14, date = "2023/10/19 - 15:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 8"),
            UnidSocial(
                id = 9, recorrId = 3, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                vAlfaS4Ad = 17, vAlfaOtrosSA = 18, vHembrasAd = 50, vCrias = 40, vDestetados = 17,
                vJuveniles = 18, vS4AdPerif = 18, vS4AdCerca = 17, vS4AdLejos = 16,
                vOtrosSAPerif = 18, vOtrosSACerca = 17, vOtrosSALejos = 16,
                mAlfaS4Ad = 17, mAlfaOtrosSA = 18, mHembrasAd = 50, mCrias = 40, mDestetados = 17,
                mJuveniles = 18, mS4AdPerif = 18, mS4AdCerca = 17, mS4AdLejos = 16,
                mOtrosSAPerif = 18, mOtrosSACerca = 17, mOtrosSALejos = 16, date = "2023/10/19 - 16:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 9"),
            UnidSocial(
                id = 10, recorrId = 3, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                vAlfaS4Ad = 19, vAlfaOtrosSA = 20, vHembrasAd = 55, vCrias = 44, vDestetados = 19,
                vJuveniles = 20, vS4AdPerif = 20, vS4AdCerca = 19, vS4AdLejos = 18,
                vOtrosSAPerif = 20, vOtrosSACerca = 19, vOtrosSALejos = 18,
                mAlfaS4Ad = 19, mAlfaOtrosSA = 20, mHembrasAd = 55, mCrias = 44, mDestetados = 19,
                mJuveniles = 20, mS4AdPerif = 20, mS4AdCerca = 19, mS4AdLejos = 18,
                mOtrosSAPerif = 20, mOtrosSACerca = 19, mOtrosSALejos = 18, date = "2023/10/19 - 13:26:48",
                latitud = -42.777, longitud = -65.048, photoPath = "", comentario = "dato test 10")
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