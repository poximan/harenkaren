package com.example.demo.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.example.demo.model.UnidSocial

data class Recorr1UnSocN(
    @Embedded val unidSocial: UnidSocial,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_recorrido"
    )
    val listaUnidSociales: List<UnidSocial>
)