package com.example.demo.compartir.importar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader

class LeerCSV(private val context: Context, private val callback: ListaImportable) {

    companion object {
        private const val TAG = "LeerCSV"
    }

    private var csvFileUri: Uri? = null
    private lateinit var pickCSVFileLauncher: ActivityResultLauncher<Intent>

    fun registerLauncher() {
        pickCSVFileLauncher = (callback as Fragment).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    csvFileUri = uri
                    readCSVFile(uri)
                }
            }
        }
    }

    fun pickCSVFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*" // Tipo MIME para archivos CSV
        }
        pickCSVFileLauncher.launch(intent)
    }

    private fun readCSVFile(uri: Uri) {

        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var headers: List<String>? = null

        val csvData = ArrayList<Map<String, String>>()

        try {
            // Leer la primera línea para obtener los encabezados
            line = reader.readLine()
            headers = line?.split(",")?.map { it.trim() }

            // Leer el resto del archivo
            while (reader.readLine().also { line = it } != null) {
                val row = line?.split(",")?.map { it.trim() }
                if (row != null && headers != null) {
                    val map = headers.zip(row).toMap()
                    if(map.size == headers.size)
                        csvData.add(map)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CSV file: ${e.message}", e)
        } finally {
            inputStream?.close()
            reader.close()
        }
        callback.onPelosReceived(csvData)
    }
}