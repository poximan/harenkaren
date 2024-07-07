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
    tableName = "unidsocial",
    foreignKeys = [ForeignKey(
        entity = Recorrido::class,
        parentColumns = ["id"],
        childColumns = ["id_recorrido"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id_recorrido"])]
)
data class UnidSocial(

    // ----- identidicadores ----- //
    @PrimaryKey
    var id: UUID,

    @ColumnInfo(name = "id_recorrido")
    var recorrId: UUID,

    var orden: Int,

    // ----- entorno ----- //
    @ColumnInfo(name = "pto_observacion")
    var ptoObsUnSoc: String,

    @ColumnInfo(name = "ctx_social")
    var ctxSocial: String,

    @ColumnInfo(name = "tpo_sustrato")
    var tpoSustrato: String,

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
    var date: String,

    var latitud: Double,

    var longitud: Double,

    // ----- otros datos ----- //
    @ColumnInfo(name = "photo_path")
    var photoPath: String?,

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
        // ----- identidicadores ----- //
        UUID.fromString(parcel.readString()),
        UUID.fromString(parcel.readString()),
        parcel.readInt(),

        // ----- entorno ----- //
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,

        // ----- vivos ----- //
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

        // ----- muertos ----- //
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

        // ----- tiempo/espacio ----- //
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    )

    @Ignore
    constructor(id: UUID, idRecorrido: UUID, estampatiempo: String) : this(
        id, idRecorrido, 0, "", "", "",
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* vivos */
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* muertos */
        estampatiempo, 0.0, 0.0, "", ""
    )

    constructor(
        id: UUID, idRecorrido: UUID,
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
    ) : this(
        id, idRecorrido, 0, ptoObsUnSoc, ctxSocial, tpoSustrato,
        vAlfaS4Ad, vAlfaSams, vHembrasAd, vCrias, vDestetados, vJuveniles,
        vS4AdPerif, vS4AdCerca, vS4AdLejos, vOtrosSamsPerif, vOtrosSamsCerca, vOtrosSamsLejos,
        mAlfaS4Ad, mAlfaSams, mHembrasAd, mCrias, mDestetados, mJuveniles,
        mS4AdPerif, mS4AdCerca, mS4AdLejos, mOtrosSamsPerif, mOtrosSamsCerca, mOtrosSamsLejos,
        timeStamp, latitud, longitud, photoPath, comentario
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(id.toString())
        parcel.writeString(ptoObsUnSoc)
        parcel.writeString(ctxSocial)
        parcel.writeString(tpoSustrato)
        parcel.writeInt(orden)
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

    /**
     * de todos los atributos de la clase, interesan los que llevan la cuenta
     * de los animales censados. incluye categorias con cuentas =0
     * @return una lista de atributos Int que llevan la cuenta de esa categoria
     */
    fun getContadores(): List<String> {

        val atributos = mutableListOf<String>()
        val campos = this.javaClass.declaredFields

        for (campo in campos) {
            val nombreCampo = campo.name
            if (nombreCampo.startsWith("v") || nombreCampo.startsWith("m")) {
                // Acceder al valor del atributo
                campo.isAccessible = true
                atributos.add(nombreCampo)
            }
        }
        return atributos
    }

    /**
     * de todos los atributos de la clase, interesan los que llevan la cuenta
     * de los animales censados. solo incluye categorias con cuentas >0
     * @return una lista de atributos Int que llevan la cuenta de esa categoria
     */
    fun getContadoresNoNulos(): List<String> {
        val atributos = mutableListOf<String>()
        val campos = listOf(
            "vAlfaS4Ad", "vAlfaSams", "vHembrasAd", "vCrias", "vDestetados", "vJuveniles",
            "vS4AdPerif", "vS4AdCerca", "vS4AdLejos", "vOtrosSamsPerif", "vOtrosSamsCerca", "vOtrosSamsLejos",
            "mAlfaS4Ad", "mAlfaSams", "mHembrasAd", "mCrias", "mDestetados", "mJuveniles",
            "mS4AdPerif", "mS4AdCerca", "mS4AdLejos", "mOtrosSamsPerif", "mOtrosSamsCerca", "mOtrosSamsLejos"
        )

        for (campo in campos) {
            val field = this.javaClass.getDeclaredField(campo)
            field.isAccessible = true
            val valor = field.getInt(this)
            if (valor > 0) {
                atributos.add(campo)
            }
        }
        return atributos
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