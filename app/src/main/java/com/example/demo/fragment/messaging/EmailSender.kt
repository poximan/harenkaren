package com.example.demo.fragment.messaging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// cienciaycoso
object EmailSender {

    fun sendEmail(destinatarios: Array<String>, asunto: String, cuerpo: String, context: Context) {
        // Crear un intent implícito para enviar correo electrónico
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")  // Se especifica el esquema "mailto:"
            putExtra(Intent.EXTRA_EMAIL, destinatarios.plus("harenkaren70@gmail.com"))  // Agregar los destinatarios del correo electrónico
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }

        // Verificar que exista una aplicación de correo electrónico para manejar el intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No se encontró una aplicación de correo electrónico en este dispositivo", Toast.LENGTH_LONG).show()
        }
    }
}

