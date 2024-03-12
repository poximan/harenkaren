package com.example.demo.model
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(foreignKeys = [ForeignKey(
        entity = Recorrido::class,
        parentColumns = ["id"],
        childColumns = ["id_recorrido"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UnidSocial(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "id_recorrido")
    var recorrId: Int,

    // ----- entorno ----- //
    @ColumnInfo(name = "pto_observacion")
    var ptoObsUnSoc: String?,

    @ColumnInfo(name = "ctx_social")
    var ctxSocial: String?,

    @ColumnInfo(name = "tpo_sustrato")
    var tpoSustrato: String?,

    // ----- dominante ----- //
    @ColumnInfo(name = "alfa_s4ad")
    var alfaS4Ad: Int,

    @ColumnInfo(name = "alfa_otros_sa")
    var alfaOtrosSA: Int,

    // ----- hembras y crias ----- //
    @ColumnInfo(name = "hembras_ad")
    var hembrasAd: Int,

    @ColumnInfo(name = "crias_vivas")
    var criasVivas: Int,

    @ColumnInfo(name = "crias_muertas")
    var criasMuertas: Int,

    @ColumnInfo(name = "destetados")
    var destetados: Int,

    @ColumnInfo(name = "juveniles")
    var juveniles: Int,

    // ----- Ad/SA proximos ----- //
    @ColumnInfo(name = "s4ad_perif")
    var s4AdPerif: Int,

    @ColumnInfo(name = "s4ad_cerca")
    var s4AdCerca: Int,

    @ColumnInfo(name = "s4ad_lejos")
    var s4AdLejos: Int,

    @ColumnInfo(name = "otros_sa_perif")
    var otrosSAPerif: Int,

    @ColumnInfo(name = "otros_sa_cerca")
    var otrosSACerca: Int,

    @ColumnInfo(name = "otros_sa_lejos")
    var otrosSALejos: Int,

    // ----- tiempo/espacio ----- //
    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name="latitude")
    var latitude: Double,

    @ColumnInfo(name="longitude")
    var longitude: Double,

    // ----- otros datos ----- //
    @ColumnInfo(name = "photo_path")
    var photoPath: String?

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    )

    @Ignore
    constructor(
        circuitoId: Int,
        latitude: Double,
        longitude: Double,
        photoPath: String
    ) : this(
        0, // Id con valor predeterminado
        circuitoId,
        null,
        null,
        null,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        null,
        latitude,
        longitude,
        photoPath
    )

    @Ignore
    constructor(
        circuitoId: Int,
        ptoObsUnSoc: String,
        ctxSocial: String,
        tpoSustrato: String,
        alfaS4Ad: Int,
        alfaOtrosSA: Int,
        hembrasAd: Int,
        criasVivas: Int,
        criasMuertas: Int,
        destetados: Int,
        juveniles: Int,
        s4AdPerif: Int,
        s4AdCerca: Int,
        s4AdLejos: Int,
        otrosSAPerif: Int,
        otrosSACerca: Int,
        otrosSALejos: Int,
        date: String,
        latitude: Double,
        longitude: Double,
        photoPath: String
    ) : this(
        0, // Id con valor predeterminado
        circuitoId,
        ptoObsUnSoc,
        ctxSocial,
        tpoSustrato,
        alfaS4Ad,
        alfaOtrosSA,
        hembrasAd,
        criasVivas,
        criasMuertas,
        destetados,
        juveniles,
        s4AdPerif,
        s4AdCerca,
        s4AdLejos,
        otrosSAPerif,
        otrosSACerca,
        otrosSALejos,
        date,
        latitude,
        longitude,
        photoPath
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(recorrId)
        parcel.writeString(ptoObsUnSoc)
        parcel.writeString(ctxSocial)
        parcel.writeString(tpoSustrato)
        parcel.writeInt(alfaS4Ad)
        parcel.writeInt(alfaOtrosSA)
        parcel.writeInt(hembrasAd)
        parcel.writeInt(criasVivas)
        parcel.writeInt(criasMuertas)
        parcel.writeInt(destetados)
        parcel.writeInt(juveniles)
        parcel.writeInt(s4AdPerif)
        parcel.writeInt(s4AdCerca)
        parcel.writeInt(s4AdLejos)
        parcel.writeInt(otrosSAPerif)
        parcel.writeInt(otrosSACerca)
        parcel.writeInt(otrosSALejos)
        parcel.writeString(date)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(photoPath)
    }

    companion object CREATOR : Parcelable.Creator<UnidSocial> {
        override fun createFromParcel(parcel: Parcel): UnidSocial {
            return UnidSocial(parcel)
        }

        override fun newArray(size: Int): Array<UnidSocial?> {
            return arrayOfNulls(size)
        }
    }

    fun setRecorrActual(id: Int) {
        recorrId = id
    }
}

