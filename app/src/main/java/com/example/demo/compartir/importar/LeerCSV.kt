package com.example.demo.compartir.importar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class LeerCSV(private val context: Context) {

    companion object {
        const val PERMISSION_REQUEST_CSV_FILE = 5
        private const val TAG = "LeerCSV"
    }

    private var csvFileUri: Uri? = null

    fun pickCSVFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv" // Tipo MIME para archivos CSV
        }
        (context as Activity).startActivityForResult(intent, PERMISSION_REQUEST_CSV_FILE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERMISSION_REQUEST_CSV_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                csvFileUri = uri
                // Procesar el archivo CSV aquí
                // Por ejemplo, leer su contenido
                readCSVFile(uri)
            }
        }
    }

    private fun readCSVFile(uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                Log.d(TAG, "CSV Line: $line")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CSV file: ${e.message}", e)
        } finally {
            inputStream?.close()
            reader.close()
        }
    }
}
