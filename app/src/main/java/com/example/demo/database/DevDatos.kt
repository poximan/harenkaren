package com.example.demo.database

import android.util.Log
import com.example.demo.dao.DiaDAO
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
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
        Log.d("DATOS DB", "se agregaron dias")
    }

    fun generarRecorridos(recorrDAO: RecorrDAO) {
        val recorrList = listOf<Recorrido>(
            Recorrido(
                id = 1, diaId = 1, observador = "hugo", fecha = "2023/10/19 - 08:20:48",
                latitudInicio = -42.555, longitudInicio = -65.031,
                latitudFin = -39.555, longitudFin = -61.031, areaRecorrida = "norte-sur"),
            Recorrido(
                id = 2, diaId = 1, observador = "sebastian", fecha = "2023/10/19 - 08:15:48",
                latitudInicio = -42.555, longitudInicio = -65.031,
                latitudFin = -38.555, longitudFin = -59.031, areaRecorrida = "suroeste-noroeste"),
            Recorrido(
                id = 3, diaId = 2, observador = "donato", fecha = "2023/10/21 - 16:15:48",
                latitudInicio = -42.000, longitudInicio = -62.371,
                latitudFin = -38.533, longitudFin = -60.311, areaRecorrida = "este-oeste"),
        )

        recorrList.forEach { recorr ->
            recorrDAO.insert(recorr)
        }
        Log.d("DATOS DB", "se agregaron recorridos")
    }

    fun generarUnidadesSociales(unsocDAO: UnSocDAO) {
        val unSocList = listOf<UnidSocial>(
            UnidSocial(id = 1, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "H", tpoSustrato = "A",
                alfaS4Ad = 1, alfaOtrosSA = 2, hembrasAd = 10, criasVivas = 8, criasMuertas = 3,
                destetados = 1, juveniles = 2, s4AdPerif = 2, s4AdCerca = 1, s4AdLejos = 0,
                otrosSAPerif = 2, otrosSACerca = 1, otrosSALejos = 0, date = "2023/10/18 - 13:26:48",
                latitude = -42.777, longitude = -65.048, photoPath = "", comentario = "dato test 1"),
            UnidSocial(id = 2, recorrId = 1, ptoObsUnSoc = "bajo", ctxSocial = "GH", tpoSustrato = "CR",
                alfaS4Ad = 1, alfaOtrosSA = 3, hembrasAd = 9, criasVivas = 12, criasMuertas = 2,
                destetados = 3, juveniles = 1, s4AdPerif = 3, s4AdCerca = 2, s4AdLejos = 1,
                otrosSAPerif = 0, otrosSACerca = 5, otrosSALejos = 4, date = "2023/10/19 - 08:26:48",
                latitude = -42.555, longitude = -65.453, photoPath = "", comentario = "dato test 2"),
            UnidSocial(id = 3, recorrId = 1, ptoObsUnSoc = "alto", ctxSocial = "PS", tpoSustrato = "M",
                alfaS4Ad = 2, alfaOtrosSA = 3, hembrasAd = 12, criasVivas = 9, criasMuertas = 4,
                destetados = 2, juveniles = 3, s4AdPerif = 3, s4AdCerca = 2, s4AdLejos = 1,
                otrosSAPerif = 3, otrosSACerca = 2, otrosSALejos = 1, date = "2023/10/18 - 14:35:20",
                latitude = -42.778, longitude = -65.049, photoPath = "", comentario = "dato test 3"),
            UnidSocial(id = 4, recorrId = 2, ptoObsUnSoc = "bajo", ctxSocial = "S", tpoSustrato = "R",
                alfaS4Ad = 3, alfaOtrosSA = 4, hembrasAd = 11, criasVivas = 10, criasMuertas = 5,
                destetados = 3, juveniles = 4, s4AdPerif = 4, s4AdCerca = 3, s4AdLejos = 2,
                otrosSAPerif = 4, otrosSACerca = 3, otrosSALejos = 2, date = "2023/10/18 - 15:44:32",
                latitude = -42.779, longitude = -65.050, photoPath = "", comentario = "dato test 4"
        ),

        UnidSocial(
            id = 5,
            recorrId = 2,
            ptoObsUnSoc = "alto",
            ctxSocial = "GH",
            tpoSustrato = "A",
            alfaS4Ad = 4,
            alfaOtrosSA = 5,
            hembrasAd = 13,
            criasVivas = 11,
            criasMuertas = 6,
            destetados = 4,
            juveniles = 5,
            s4AdPerif = 5,
            s4AdCerca = 4,
            s4AdLejos = 3,
            otrosSAPerif = 5,
            otrosSACerca = 4,
            otrosSALejos = 3,
            date = "2023/10/18 - 16:53:44",
            latitude = -42.780,
            longitude = -65.051,
            photoPath = "",
            comentario = "dato test 5"
        ),

        UnidSocial(
            id = 6,
            recorrId = 2,
            ptoObsUnSoc = "bajo",
            ctxSocial = "H",
            tpoSustrato = "CR",
            alfaS4Ad = 5,
            alfaOtrosSA = 1,
            hembrasAd = 14,
            criasVivas = 12,
            criasMuertas = 7,
            destetados = 5,
            juveniles = 6,
            s4AdPerif = 1,
            s4AdCerca = 5,
            s4AdLejos = 4,
            otrosSAPerif = 1,
            otrosSACerca = 5,
            otrosSALejos = 4,
            date = "2023/10/18 - 17:35:12",
            latitude = -42.781,
            longitude = -65.052,
            photoPath = "",
            comentario = "dato test 6"
        ),

        UnidSocial(
            id = 7,
            recorrId = 3,
            ptoObsUnSoc = "alto",
            ctxSocial = "H",
            tpoSustrato = "M",
            alfaS4Ad = 1,
            alfaOtrosSA = 2,
            hembrasAd = 15,
            criasVivas = 13,
            criasMuertas = 8,
            destetados = 6,
            juveniles = 7,
            s4AdPerif = 2,
            s4AdCerca = 1,
            s4AdLejos = 5,
            otrosSAPerif = 2,
            otrosSACerca = 1,
            otrosSALejos = 5,
            date = "2023/10/18 - 18:24:59",
            latitude = -42.782,
            longitude = -65.053,
            photoPath = "",
            comentario = "dato test 7"
        ),

        UnidSocial(
            id = 8,
            recorrId = 3,
            ptoObsUnSoc = "bajo",
            ctxSocial = "H",
            tpoSustrato = "R",
            alfaS4Ad = 2,
            alfaOtrosSA = 3,
            hembrasAd = 16,
            criasVivas = 14,
            criasMuertas = 9,
            destetados = 7,
            juveniles = 8,
            s4AdPerif = 3,
            s4AdCerca = 2,
            s4AdLejos = 1,
            otrosSAPerif = 3,
            otrosSACerca = 2,
            otrosSALejos = 1,
            date = "2023/10/18 - 19:17:28",
            latitude = -42.783,
            longitude = -65.054,
            photoPath = "",
            comentario = "dato test 8"
        )
        )

        unSocList.forEach { unsoc ->
            unsocDAO.insert(unsoc)
        }
        Log.d("DATOS DB", "se agregaron unidades sociales")
    }

    fun vaciarDias(diaDAO: DiaDAO) {
        diaDAO.deleteAll()
        Log.d("DATOS DB", "se borraron los dias")
    }

    fun vaciarRecorridos(recorrDAO: RecorrDAO) {
        recorrDAO.deleteAll()
        Log.d("DATOS DB", "se borraron los recorridos")
    }

    fun vaciarUnidadesSociales(unsocDAO: UnSocDAO) {
        unsocDAO.deleteAll()
        Log.d("DATOS DB", "se borraron las unidades sociales")
    }
}