package com.example.demo.model
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "censo_table",
    foreignKeys = [ForeignKey(
        entity = Circuito::class,
        parentColumns = ["id"],
        childColumns = ["circuito_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Censo(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "circuito_id")
    val circuitoId: Int,

    // ----- entorno ----- //
    @ColumnInfo(name = "pto_obs_censo")
    var ptoObsCenso: String,

    @ColumnInfo(name = "ctx_social")
    var ctxSocial: String,

    @ColumnInfo(name = "tpo_sustrato")
    var tpoSustrato: String,

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
    var date: String,

    @ColumnInfo(name="latitude")
    var latitude: Double?,

    @ColumnInfo(name="longitude")
    var longitude: Double?,

    // ----- otros datos ----- //
    @ColumnInfo(name = "photo_path")
    var photoPath: String

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
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
        parcel.readString().toString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(circuitoId)
        parcel.writeString(ptoObsCenso)
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
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(photoPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Censo> {
        override fun createFromParcel(parcel: Parcel): Censo {
            return Censo(parcel)
        }

        override fun newArray(size: Int): Array<Censo?> {
            return arrayOfNulls(size)
        }
    }
}

