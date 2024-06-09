package com.example.demo.fragment.messaging

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.demo.R
import java.io.File

// cienciaycoso
object EmailSender {

    fun sendEmail(cuerpo: String, archivoAdjunto: File, context: Context) {

        val destinatarios = arrayOf("harenkaren70@gmail.com")

        val asunto = context.getString(R.string.mai_asunto)
        val uri =
            FileProvider.getUriForFile(context, "com.example.demo.fileprovider", archivoAdjunto)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"  // Esto asegura que se abran aplicaciones de correo electrónico
            putExtra(Intent.EXTRA_EMAIL, destinatarios)
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
            putExtra(Intent.EXTRA_STREAM, uri)  // Adjuntar el archivo
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Concede permiso de lectura al destinatario
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.mai_appemail),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

