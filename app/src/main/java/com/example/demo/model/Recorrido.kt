package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recorrido",
    foreignKeys = [ForeignKey(
        entity = Dia::class,
        parentColumns = ["id"],
        childColumns = ["id_dia"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Recorrido(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "id_dia")
    var diaId: Int,

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
    var areaRecorrida: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
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
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recorrido> {
        override fun createFromParcel(parcel: Parcel): Recorrido {
            return Recorrido(parcel)
        }

        override fun newArray(size: Int): Array<Recorrido?> {
            return arrayOfNulls(size)
        }
    }
}

