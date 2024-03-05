package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circuito_table")
data class Circuito(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    // ----- entorno ----- //
    @ColumnInfo(name = "observador")
    var observador: String,

    // ----- tiempo ----- //
    @ColumnInfo(name = "fecha")
    var fecha : String,

    @ColumnInfo(name="latitud_inicio")
    var latitudInicio: Double?,

    @ColumnInfo(name="longitud_inicio")
    var longitudInicio: Double?,

    @ColumnInfo(name="latitud_fin")
    var latitudFin: Double?,

    @ColumnInfo(name="longitud_fin")
    var longitudFin: Double?,

    // ----- espacio ----- //
    @ColumnInfo(name = "area_recorrida")
    var areaRecorrida: String,

    @ColumnInfo(name = "meteo")
    var meteo: String


    //    val listaCensos: MutableList<Censo> = mutableListOf()

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        // ----- entorno ----- //
        parcel.readString().toString(),
        // ----- tiempo ----- //
        parcel.readString().toString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        // ----- espacio ----- //
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(observador)
        parcel.writeString(fecha)
        parcel.writeValue(latitudInicio)
        parcel.writeValue(longitudInicio)
        parcel.writeValue(latitudFin)
        parcel.writeValue(longitudFin)
        parcel.writeString(areaRecorrida)
        parcel.writeString(meteo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Circuito> {
        override fun createFromParcel(parcel: Parcel): Circuito {
            return Circuito(parcel)
        }

        override fun newArray(size: Int): Array<Circuito?> {
            return arrayOfNulls(size)
        }
    }
}

