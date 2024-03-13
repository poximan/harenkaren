package com.example.demo.model

import androidx.room.Embedded
import androidx.room.Relation

data class RecorrConUnSoc(

    @Embedded
    val recorrido: Recorrido,

    @Relation(
        parentColumn = "id",
        entityColumn = "id_recorrido"
    )
    val listaUnidSociales: List<UnidSocial>

)