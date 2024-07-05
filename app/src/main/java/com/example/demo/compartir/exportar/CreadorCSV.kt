package com.example.demo.compartir.exportar

import android.content.Context
import com.example.demo.model.EntidadesPlanas
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreadorCSV {

    companion object {
        const val PERMISSION_REQUEST_WRITE_EXTERNAL = 5
    }

    // Función para generar una cadena CSV a partir de una lista de EntidadesPlanas
    private fun generarCSV(entidades: List<EntidadesPlanas>): String {
        val header =
            "celular_id,dia_id,dia_orden,dia_fecha,recorr_id," +
                    "recorr_orden,observador,recorr_fecha_ini,recorr_fecha_fin," +
                    "recorr_latitud_ini,recorr_longitud_ini," +
                    "recorr_latitud_fin,recorr_longitud_fin,area_recorrida," +
                    "meteo,marea,observaciones,unsoc_id," +
                    "unsoc_orden,pto_observacion,ctx_social,tpo_sustrato," +
                    "v_alfa_s4ad,v_alfa_sams,v_hembras_ad," +
                    "v_crias,v_destetados,v_juveniles," +
                    "v_s4ad_perif,v_s4ad_cerca,v_s4ad_lejos," +
                    "v_otros_sams_perif,v_otros_sams_cerca,v_otros_sams_lejos," +
                    "m_alfa_s4ad,m_alfa_sams,m_hembras_ad," +
                    "m_crias,m_destetados,m_juveniles," +
                    "m_s4ad_perif,m_s4ad_cerca,m_s4ad_lejos," +
                    "m_otros_sams_perif,m_otros_sams_cerca,m_otros_sams_lejos," +
                    "unsoc_fecha,unsoc_latitud,unsoc_longitud,photo_path,comentario\n"

        val csvRows = entidades.joinToString("\n") { entidad ->
            "${entidad.celular_id},${entidad.dia_id},${entidad.dia_orden},${entidad.dia_fecha},${entidad.recorr_id}," +
                    "${entidad.recorr_orden},${entidad.observador},${entidad.recorr_fecha_ini},${entidad.recorr_fecha_fin}," +
                    "${entidad.recorr_latitud_ini},${entidad.recorr_longitud_ini}," +
                    "${entidad.recorr_latitud_fin},${entidad.recorr_longitud_fin},${entidad.area_recorrida}," +
                    "${entidad.meteo},${entidad.marea},${entidad.observaciones},${entidad.unsoc_id}," +
                    "${entidad.unsoc_orden},${entidad.pto_observacion},${entidad.ctx_social},${entidad.tpo_sustrato}," +
                    "${entidad.v_alfa_s4ad},${entidad.v_alfa_sams},${entidad.v_hembras_ad}," +
                    "${entidad.v_crias},${entidad.v_destetados},${entidad.v_juveniles}," +
                    "${entidad.v_s4ad_perif},${entidad.v_s4ad_cerca},${entidad.v_s4ad_lejos}," +
                    "${entidad.v_otros_sams_perif},${entidad.v_otros_sams_cerca},${entidad.v_otros_sams_lejos}," +
                    "${entidad.m_alfa_s4ad},${entidad.m_alfa_sams},${entidad.m_hembras_ad}," +
                    "${entidad.m_crias},${entidad.m_destetados},${entidad.m_juveniles}," +
                    "${entidad.m_s4ad_perif},${entidad.m_s4ad_cerca},${entidad.m_s4ad_lejos}," +
                    "${entidad.m_otros_sams_perif},${entidad.m_otros_sams_cerca},${entidad.m_otros_sams_lejos}," +
                    "${entidad.unsoc_fecha},${entidad.unsoc_latitud},${entidad.unsoc_longitud},${entidad.photo_path},${entidad.comentario}"
        }
        return header + csvRows
    }

    fun empaquetarCSV(context: Context, entidadesList: List<EntidadesPlanas>): File {
        val csvData = generarCSV(entidadesList)

        // Obtener el directorio de archivos privados de la aplicación
        val directorioArchivos = context.filesDir

        // Crear el archivo CSV
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())
        val nombreArchivo = "censos_$timeStamp.csv"
        val archivo = File(directorioArchivos, nombreArchivo)

        // Escribir los datos CSV en el archivo
        archivo.writeText(csvData)

        return archivo
    }
}