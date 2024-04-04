package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "unidsocial",
    foreignKeys = [ForeignKey(
        entity = Recorrido::class,
        parentColumns = ["id"],
        childColumns = ["id_recorrido"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UnidSocial(

    @PrimaryKey(autoGenerate = true)
    var id: Int?,

    @ColumnInfo(name = "id_recorrido")
    var recorrId: Int,

    // ----- entorno ----- //
    @ColumnInfo(name = "pto_observacion")
    var ptoObsUnSoc: String?,

    @ColumnInfo(name = "ctx_social")
    var ctxSocial: String?,

    @ColumnInfo(name = "tpo_sustrato")
    var tpoSustrato: String?,

    /* =========================================
    ================== VIVIOS ==================
    ========================================= */
    
    // ----- dominante ----- //
    @ColumnInfo(name = "v_alfa_s4ad")
    var vAlfaS4Ad: Int,

    @ColumnInfo(name = "v_alfa_sams")
    var vAlfaSams: Int,

    // ----- hembras y crias ----- //
    @ColumnInfo(name = "v_hembras_ad")
    var vHembrasAd: Int,

    @ColumnInfo(name = "v_crias")
    var vCrias: Int,

    @ColumnInfo(name = "v_destetados")
    var vDestetados: Int,

    @ColumnInfo(name = "v_juveniles")
    var vJuveniles: Int,

    // ----- Ad/SA proximos ----- //
    @ColumnInfo(name = "v_s4ad_perif")
    var vS4AdPerif: Int,

    @ColumnInfo(name = "v_s4ad_cerca")
    var vS4AdCerca: Int,

    @ColumnInfo(name = "v_s4ad_lejos")
    var vS4AdLejos: Int,

    @ColumnInfo(name = "v_otros_sams_perif")
    var vOtrosSamsPerif: Int,

    @ColumnInfo(name = "v_otros_sams_cerca")
    var vOtrosSamsCerca: Int,

    @ColumnInfo(name = "v_otros_sams_lejos")
    var vOtrosSamsLejos: Int,

    /* =========================================
   ================== MUERTOS ==================
   ========================================= */

    // ----- dominante ----- //
    @ColumnInfo(name = "m_alfa_s4ad")
    var mAlfaS4Ad: Int,

    @ColumnInfo(name = "m_alfa_sams")
    var mAlfaSams: Int,

    // ----- hembras y crias ----- //
    @ColumnInfo(name = "m_hembras_ad")
    var mHembrasAd: Int,

    @ColumnInfo(name = "m_crias")
    var mCrias: Int,

    @ColumnInfo(name = "m_destetados")
    var mDestetados: Int,

    @ColumnInfo(name = "m_juveniles")
    var mJuveniles: Int,

    // ----- Ad/SA proximos ----- //
    @ColumnInfo(name = "m_s4ad_perif")
    var mS4AdPerif: Int,

    @ColumnInfo(name = "m_s4ad_cerca")
    var mS4AdCerca: Int,

    @ColumnInfo(name = "m_s4ad_lejos")
    var mS4AdLejos: Int,

    @ColumnInfo(name = "m_otros_sams_perif")
    var mOtrosSamsPerif: Int,

    @ColumnInfo(name = "m_otros_sams_cerca")
    var mOtrosSamsCerca: Int,

    @ColumnInfo(name = "m_otros_sams_lejos")
    var mOtrosSamsLejos: Int,

    // ----- tiempo/espacio ----- //
    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name="latitud")
    var latitud: Double,

    @ColumnInfo(name="longitud")
    var longitud: Double,

    // ----- otros datos ----- //
    @ColumnInfo(name = "photo_path")
    var photoPath: String?,

    @ColumnInfo(name = "comentario")
    var comentario: String?
) : Parcelable {
/*
Parcelable es una interfaz para serializar objetos. util para transferirlos entre actividades y fragmentos,
o entre aplicaciones a través de Intent o Bundle. Es un objeto enviado como flujo de bytes.

La interfaz requiere writeToParcel() para escribir el estado de un objeto en un Parcel,
y createFromParcel() para crear una nueva instancia del objeto a partir de un Parcel.
 */
    @Ignore
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
        parcel.readString(),
        parcel.readString()
    ) {
    }

    @Ignore
    constructor(idRecorrido: Int, estampatiempo: String) : this(
        null,idRecorrido,null, null, null,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* vivos */
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* muertos */
        estampatiempo, 0.0, 0.0, "","")

    constructor(
        idRecorrido: Int,
        ptoObsUnSoc: String, ctxSocial: String, tpoSustrato: String,
        vAlfaS4Ad: Int, vAlfaSams: Int, vHembrasAd: Int, vCrias: Int,
        vDestetados: Int, vJuveniles: Int, vS4AdPerif: Int, vS4AdCerca: Int,
        vS4AdLejos: Int, vOtrosSamsPerif: Int, vOtrosSamsCerca: Int, vOtrosSamsLejos: Int,
        mAlfaS4Ad: Int, mAlfaSams: Int, mHembrasAd: Int, mCrias: Int,
        mDestetados: Int, mJuveniles: Int, mS4AdPerif: Int, mS4AdCerca: Int,
        mS4AdLejos: Int, mOtrosSamsPerif: Int, mOtrosSamsCerca: Int, mOtrosSamsLejos: Int,
        timeStamp: String,
        latitud: Double, longitud: Double,
        photoPath: String,
        comentario: String
    ) : this(null, idRecorrido, ptoObsUnSoc, ctxSocial, tpoSustrato,
        vAlfaS4Ad, vAlfaSams, vHembrasAd, vCrias, vDestetados, vJuveniles,
        vS4AdPerif, vS4AdCerca, vS4AdLejos, vOtrosSamsPerif, vOtrosSamsCerca, vOtrosSamsLejos,
        mAlfaS4Ad, mAlfaSams, mHembrasAd, mCrias, mDestetados, mJuveniles,
        mS4AdPerif, mS4AdCerca, mS4AdLejos, mOtrosSamsPerif, mOtrosSamsCerca, mOtrosSamsLejos,
        timeStamp, latitud, longitud, photoPath, comentario)

    override fun describeContents(): Int {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        id?.let { parcel.writeInt(it) }
        parcel.writeInt(recorrId)
        parcel.writeString(ptoObsUnSoc)
        parcel.writeString(ctxSocial)
        parcel.writeString(tpoSustrato)
        /* VIVAS */
        parcel.writeInt(vAlfaS4Ad)
        parcel.writeInt(vAlfaSams)
        parcel.writeInt(vHembrasAd)
        parcel.writeInt(vCrias)
        parcel.writeInt(vDestetados)
        parcel.writeInt(vJuveniles)
        parcel.writeInt(vS4AdPerif)
        parcel.writeInt(vS4AdCerca)
        parcel.writeInt(vS4AdLejos)
        parcel.writeInt(vOtrosSamsPerif)
        parcel.writeInt(vOtrosSamsCerca)
        parcel.writeInt(vOtrosSamsLejos)
        /* MUERTAS */
        parcel.writeInt(mAlfaS4Ad)
        parcel.writeInt(mAlfaSams)
        parcel.writeInt(mHembrasAd)
        parcel.writeInt(mCrias)
        parcel.writeInt(mDestetados)
        parcel.writeInt(mJuveniles)
        parcel.writeInt(mS4AdPerif)
        parcel.writeInt(mS4AdCerca)
        parcel.writeInt(mS4AdLejos)
        parcel.writeInt(mOtrosSamsPerif)
        parcel.writeInt(mOtrosSamsCerca)
        parcel.writeInt(mOtrosSamsLejos)
        parcel.writeString(date)
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
        parcel.writeString(photoPath)
        parcel.writeString(comentario)
    }

    companion object CREATOR : Parcelable.Creator<UnidSocial> {
        override fun createFromParcel(parcel: Parcel): UnidSocial {
            return UnidSocial(parcel)
        }

        override fun newArray(size: Int): Array<UnidSocial?> {
            return arrayOfNulls(size)
        }
    }
}
