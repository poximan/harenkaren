package phocidae.mirounga.leonina.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.UUID

data class EntidadesPlanas(
    val celular_id: String,
    val dia_id: UUID,
    val dia_orden: Int,
    val dia_fecha: String,
    /* recorrido */
    var recorr_id: UUID,
    val recorr_id_dia: UUID,
    val recorr_orden: Int,
    val observador: String,
    val recorr_fecha_ini: String,
    val recorr_fecha_fin: String,
    val recorr_latitud_ini: Double,
    val recorr_longitud_ini: Double,
    val recorr_latitud_fin: Double,
    val recorr_longitud_fin: Double,
    val area_recorrida: String,
    val meteo: String,
    val marea: String,
    val observaciones: String,
    /* unidad social */
    val unsoc_id: UUID,
    var unsoc_id_recorr: UUID,
    val unsoc_orden: Int,
    val pto_observacion: String,
    val ctx_social: String,
    val tpo_sustrato: String,
    /* Propiedades VIVIOS */
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
    /* Propiedades MUERTOS */
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
    /* Propiedades adicionales */
    val unsoc_fecha: String,
    val unsoc_latitud: Double,
    val unsoc_longitud: Double,
    val comentario: String
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        /* dia */
        parcel.readString()!!,
        UUID.fromString(parcel.readString()),
        parcel.readInt(),
        parcel.readString()!!,
        /* recorrido */
        UUID.fromString(parcel.readString()),
        UUID.fromString(parcel.readString()),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        /* unidad social */
        UUID.fromString(parcel.readString()),
        UUID.fromString(parcel.readString()),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        /* vivos */
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
        /* muertos */
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
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        /* dia */
        parcel.writeString(celular_id)
        parcel.writeString(dia_id.toString())
        parcel.writeInt(dia_orden)
        parcel.writeString(dia_fecha)
        /* recorrido */
        parcel.writeString(recorr_id.toString())
        parcel.writeString(recorr_id_dia.toString())
        parcel.writeInt(recorr_orden)
        parcel.writeString(observador)
        parcel.writeString(recorr_fecha_ini)
        parcel.writeString(recorr_fecha_fin)
        parcel.writeDouble(recorr_latitud_ini)
        parcel.writeDouble(recorr_longitud_ini)
        parcel.writeDouble(recorr_latitud_fin)
        parcel.writeDouble(recorr_longitud_fin)
        parcel.writeString(area_recorrida)
        parcel.writeString(meteo)
        parcel.writeString(marea)
        parcel.writeString(observaciones)
        /* unidad social */
        parcel.writeString(unsoc_id.toString())
        parcel.writeString(unsoc_id_recorr.toString())
        parcel.writeInt(unsoc_orden)
        parcel.writeString(pto_observacion)
        parcel.writeString(ctx_social)
        parcel.writeString(tpo_sustrato)
        /* vivos */
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
        /* muertos */
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
        parcel.writeString(unsoc_fecha)
        parcel.writeDouble(unsoc_latitud)
        parcel.writeDouble(unsoc_longitud)
        parcel.writeString(comentario)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getDia(): Dia {
        return Dia(celularId = celular_id, id = dia_id, fecha = dia_fecha)
    }

    fun getRecorrido(): Recorrido {
        return Recorrido(
            id = recorr_id,
            diaId = recorr_id_dia,
            observador = observador,
            fechaIni = recorr_fecha_ini,
            fechaFin = recorr_fecha_fin,
            latitudIni = recorr_latitud_ini,
            longitudIni = recorr_longitud_ini,
            latitudFin = recorr_latitud_fin,
            longitudFin = recorr_longitud_fin,
            area_recorrida,
            meteo,
            marea,
            observaciones
        )
    }

    fun getUnidSocial(): UnidSocial {
        return UnidSocial(
            id = unsoc_id,
            recorrId = unsoc_id_recorr,
            unsoc_orden,
            pto_observacion,
            ctx_social,
            tpo_sustrato,
            v_alfa_s4ad,
            v_alfa_sams,
            v_hembras_ad,
            v_crias,
            v_destetados,
            v_juveniles,
            v_s4ad_perif,
            v_s4ad_cerca,
            v_s4ad_lejos,
            v_otros_sams_perif,
            v_otros_sams_cerca,
            v_otros_sams_lejos,
            m_alfa_s4ad,
            m_alfa_sams,
            m_hembras_ad,
            m_crias,
            m_destetados,
            m_juveniles,
            m_s4ad_perif,
            m_s4ad_cerca,
            m_s4ad_lejos,
            m_otros_sams_perif,
            m_otros_sams_cerca,
            m_otros_sams_lejos,
            date = unsoc_fecha,
            latitud = unsoc_latitud,
            longitud = unsoc_longitud,
            comentario
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