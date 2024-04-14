package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "dia")
data class Dia (

    @ColumnInfo(name = "id_celular")
    var celularId: String,

    @PrimaryKey
    var id: UUID,

    // ----- tiempo ----- //
    @ColumnInfo(name = "fecha")
    var fecha : String,

    // ----- espacio ----- //
    @ColumnInfo(name = "meteo")
    var meteo: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        UUID.fromString(parcel.readString()),

        // ----- tiempo ----- //
        parcel.readString().toString(),

        // ----- espacio ----- //
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(celularId)
        parcel.writeString(id.toString())
        parcel.writeString(fecha)
        parcel.writeString(meteo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dia> {
        override fun createFromParcel(parcel: Parcel): Dia {
            return Dia(parcel)
        }

        override fun newArray(size: Int): Array<Dia?> {
            return arrayOfNulls(size)
        }
    }
}

