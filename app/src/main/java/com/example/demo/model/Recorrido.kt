package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "recorrido",
    foreignKeys = [ForeignKey(
        entity = Dia::class,
        parentColumns = ["id"],
        childColumns = ["id_dia"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id_dia"])]
)
data class Recorrido(

    @PrimaryKey(autoGenerate = true)
    var id: Int?,

    @ColumnInfo(name = "id_dia")
    var diaId: UUID,

    // ----- entorno ----- //
    @ColumnInfo(name = "observador")
    var observador: String,

    // ----- tiempo ----- //
    @ColumnInfo(name = "fecha_ini")
    var fechaIni: String,

    @ColumnInfo(name = "fecha_fin")
    var fechaFin: String,

    // ----- espacio ----- //
    @ColumnInfo(name="latitud_ini")
    var latitudIni: Double?,

    @ColumnInfo(name="longitud_ini")
    var longitudIni: Double?,

    @ColumnInfo(name="latitud_fin")
    var latitudFin: Double?,

    @ColumnInfo(name="longitud_fin")
    var longitudFin: Double?,

    @ColumnInfo(name = "area_recorrida")
    var areaRecorrida: String

):Parcelable {

    @Ignore
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        UUID.fromString(parcel.readString()),
        // ----- entorno ----- //
        parcel.readString().toString(),
        // ----- tiempo ----- //
        parcel.readString().toString(),
        parcel.readString().toString(),
        // ----- espacio ----- //
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString().toString()
    )

    constructor(idDia: UUID, observador: String, fechaIni: String,
                latitudIni: Double, longitudIni: Double, areaRecorrida: String
    ): this(
        null, idDia, observador, fechaIni, "",
        latitudIni, longitudIni, 0.0, 0.0, areaRecorrida)

    override fun describeContents(): Int {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        id?.let { parcel.writeInt(it) }
        parcel.writeString(observador)
        parcel.writeString(fechaIni)
        parcel.writeString(fechaFin)
        parcel.writeValue(latitudIni)
        parcel.writeValue(longitudIni)
        parcel.writeValue(latitudFin)
        parcel.writeValue(longitudFin)
        parcel.writeString(areaRecorrida)
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

