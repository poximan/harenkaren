package com.example.demo.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.UUID

data class EntidadesPlanas(
    val celular_id: String?,
    val dia_id: UUID,
    val dia_fecha: String?,
    val meteo: String?,
    val recorr_id: Int,
    val observador: String?,
    val recorr_fecha_ini: String?,
    val recorr_fecha_fin: String?,
    val recorr_latitud_ini: Double,
    val recorr_longitud_ini: Double,
    val recorr_latitud_fin: Double,
    val recorr_longitud_fin: Double,
    val area_recorrida: String?,
    val unidsocial_id: Int,
    val pto_observacion: String?,
    val ctx_social: String?,
    val tpo_sustrato: String?,
    // Propiedades VIVIOS
    val v_alfa_s4ad: Int,
    val v_alfa_sams: Int,
    val v_hembras_ad: Int,
    val v_crias: Int,
    val v_destetados: Int,
    val v_juveniles: Int,
    val v_s4ad_perif: Int,
    val v_s4ad_cerca: Int,
    val v_s4ad_lejos: Int,
    val v_otros_sams_perif: Int,
    val v_otros_sams_cerca: Int,
    val v_otros_sams_lejos: Int,
    // Propiedades MUERTOS
    val m_alfa_s4ad: Int,
    val m_alfa_sams: Int,
    val m_hembras_ad: Int,
    val m_crias: Int,
    val m_destetados: Int,
    val m_juveniles: Int,
    val m_s4ad_perif: Int,
    val m_s4ad_cerca: Int,
    val m_s4ad_lejos: Int,
    val m_otros_sams_perif: Int,
    val m_otros_sams_cerca: Int,
    val m_otros_sams_lejos: Int,
    // Propiedades adicionales
    val unidsocial_fecha: String?,
    val unidsocial_latitud: Double,
    val unidsocial_longitud: Double,
    val photo_path: String?,
    val comentario: String?
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        UUID.fromString(parcel.readString()),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(celular_id)
        parcel.writeString(dia_id.toString())
        parcel.writeString(dia_fecha)
        parcel.writeString(meteo)
        parcel.writeInt(recorr_id)
        parcel.writeString(observador)
        parcel.writeString(recorr_fecha_ini)
        parcel.writeString(recorr_fecha_fin)
        parcel.writeDouble(recorr_latitud_ini)
        parcel.writeDouble(recorr_longitud_ini)
        parcel.writeDouble(recorr_latitud_fin)
        parcel.writeDouble(recorr_longitud_fin)
        parcel.writeString(area_recorrida)
        parcel.writeInt(unidsocial_id)
        parcel.writeString(pto_observacion)
        parcel.writeString(ctx_social)
        parcel.writeString(tpo_sustrato)
        parcel.writeInt(v_alfa_s4ad)
        parcel.writeInt(v_alfa_sams)
        parcel.writeInt(v_hembras_ad)
        parcel.writeInt(v_crias)
        parcel.writeInt(v_destetados)
        parcel.writeInt(v_juveniles)
        parcel.writeInt(v_s4ad_perif)
        parcel.writeInt(v_s4ad_cerca)
        parcel.writeInt(v_s4ad_lejos)
        parcel.writeInt(v_otros_sams_perif)
        parcel.writeInt(v_otros_sams_cerca)
        parcel.writeInt(v_otros_sams_lejos)
        parcel.writeInt(m_alfa_s4ad)
        parcel.writeInt(m_alfa_sams)
        parcel.writeInt(m_hembras_ad)
        parcel.writeInt(m_crias)
        parcel.writeInt(m_destetados)
        parcel.writeInt(m_juveniles)
        parcel.writeInt(m_s4ad_perif)
        parcel.writeInt(m_s4ad_cerca)
        parcel.writeInt(m_s4ad_lejos)
        parcel.writeInt(m_otros_sams_perif)
        parcel.writeInt(m_otros_sams_cerca)
        parcel.writeInt(m_otros_sams_lejos)
        parcel.writeString(unidsocial_fecha)
        parcel.writeDouble(unidsocial_latitud)
        parcel.writeDouble(unidsocial_longitud)
        parcel.writeString(photo_path)
        parcel.writeString(comentario)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getDia(): Dia {
        return Dia(
            celularId = celular_id!!, id = dia_id,
            fecha = dia_fecha!!, meteo = meteo!!
        )
    }

    fun getRecorrido(): Recorrido {
        return Recorrido(
            dia_id, observador!!, recorr_fecha_ini!!, recorr_fecha_fin!!,
            recorr_latitud_ini, recorr_latitud_fin, recorr_longitud_ini, recorr_longitud_fin,
            area_recorrida!!
        )
    }

    companion object CREATOR : Parcelable.Creator<EntidadesPlanas> {
        override fun createFromParcel(parcel: Parcel): EntidadesPlanas {
            return EntidadesPlanas(parcel)
        }

        override fun newArray(size: Int): Array<EntidadesPlanas?> {
            return arrayOfNulls(size)
        }
    }
}