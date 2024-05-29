package com.example.demo.servicios

import android.content.Context
import com.example.demo.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class FechaOficial(private val context: Context) {

    fun transformar(fecha: String): String {
        // Lista de posibles patrones de entrada
        val patronesEntrada = listOf("yyyy-MM-dd", "dd/MM/yyyy", "MM-dd-yyyy", "yyyy/MM/dd")

        // formato oficial
        val formatoRecurso = context.getString(R.string.formato_fecha)
        val formatoSalida = SimpleDateFormat(formatoRecurso, Locale.getDefault())

        for (patron in patronesEntrada) {
            val formatoEntrada = SimpleDateFormat(patron, Locale.getDefault())
            try {
                val date = formatoEntrada.parse(fecha)
                if (date != null) {
                    // Formatear la fecha en el nuevo formato y devolverla
                    return formatoSalida.format(date)
                }
            } catch (e: ParseException) { }
        }
        return "Fecha inválida"
    }
}
