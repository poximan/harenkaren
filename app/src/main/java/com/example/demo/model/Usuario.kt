package com.example.demo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "usuario")
data class Usuario(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var email: String,
    var pass: String,
    var esAdmin: Boolean
) : Serializable

