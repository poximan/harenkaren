package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
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

    // ----- identidicadores ----- //
    @PrimaryKey(autoGenerate = true)
    var id: Int?,

    @ColumnInfo(name = "id_dia")
    var diaId: UUID,

    @ColumnInfo(name = "cont_inst")
    var contadorInstancias: Int,

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
    var areaRecorrida: String,

    @ColumnInfo(name = "meteo")
    var meteo: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        // ----- identidicadores ----- //
        parcel.readInt(),
        UUID.fromString(parcel.readString()),
        parcel.readInt(),
        parcel.readString().toString(),

        // ----- tiempo ----- //
        parcel.readString().toString(),
        parcel.readString().toString(),

        // ----- espacio ----- //
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    constructor(idDia: UUID, observador: String, fechaIni: String,
                latitudIni: Double, longitudIni: Double, areaRecorrida: String, meteo: String
    ): this(
        null, idDia, 0, observador, fechaIni, "",
        latitudIni, longitudIni, 0.0, 0.0, areaRecorrida, meteo)

    constructor(
        diaId: UUID,
        observador: String,
        recorrFechaIni: String,
        recorrFechaFin: String,
        recorrLatitudIni: Double,
        recorrLatitudFin: Double,
        recorrLongitudIni: Double,
        recorrLongitudFin: Double,
        areaRecorrida: String,
        meteo: String
    ) : this(null, diaId, 0, observador, recorrFechaIni, recorrFechaFin,
        recorrLatitudIni, recorrLongitudIni, recorrLatitudFin, recorrLongitudFin,
        areaRecorrida, meteo)

    override fun describeContents(): Int {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // ----- identidicadores ----- //
        id?.let { parcel.writeInt(it) }
        parcel.writeString(diaId.toString())
        parcel.writeInt(contadorInstancias)
        parcel.writeString(observador)

        // ----- tiempo ----- //
        parcel.writeString(fechaIni)
        parcel.writeString(fechaFin)

        // ----- espacio ----- //
        parcel.writeValue(latitudIni)
        parcel.writeValue(longitudIni)
        parcel.writeValue(latitudFin)
        parcel.writeValue(longitudFin)
        parcel.writeString(areaRecorrida)
        parcel.writeString(meteo)
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

