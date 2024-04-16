package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "dia")
data class Dia (

    // ----- identidicadores ----- //
    @ColumnInfo(name = "id_celular")
    var celularId: String,

    @PrimaryKey
    var id: UUID,

    @ColumnInfo(name = "cont_inst")
    var contadorInstancias: Int,

    // ----- tiempo ----- //
    @ColumnInfo(name = "fecha")
    var fecha : String,

    // ----- espacio ----- //
    @ColumnInfo(name = "meteo")
    var meteo: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        // ----- identidicadores ----- //
        parcel.readString().toString(),
        UUID.fromString(parcel.readString()),
        parcel.readInt(),

        // ----- tiempo/espacio ----- //
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    @Ignore
    constructor(celularId: String, id: UUID, fecha: String, meteo: String) :
            this(celularId, id, 0, fecha, meteo)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // ----- identidicadores ----- //
        parcel.writeString(celularId)
        parcel.writeString(id.toString())
        parcel.writeInt(contadorInstancias)

        // ----- tiempo/espacio ----- //
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

