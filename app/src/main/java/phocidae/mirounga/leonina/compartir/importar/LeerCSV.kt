package phocidae.mirounga.leonina.compartir.importar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LeerCSV(private val context: Context, private val callback: ListaImportable) {

    companion object {
        private const val TAG = "LeerCSV"
    }

    private var csvFileUri: Uri? = null
    private lateinit var pickCSVFileLauncher: ActivityResultLauncher<Intent>

    fun registerLauncher() {
        Log.i(TAG, "registrando lanzador")

        pickCSVFileLauncher = (callback as Fragment).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    Log.i(TAG, "retorno desde app remota")

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

        // Leer la primera línea para obtener los encabezados
        var line = reader.readLine()
        val headers: List<String>? = line?.split(",")?.map { it.trim() }

        val csvData = ArrayList<Map<String, String>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.statSize
                    ?: 1 // Evitar división por cero
                var bytesRead = line?.length?.toFloat() ?: 0f

                // Leer el resto del archivo
                while (reader.readLine().also { line = it } != null) {
                    // Calcular y actualizar el progreso
                    bytesRead += line.length
                    val progress = (bytesRead / fileSize) * 100

                    withContext(Dispatchers.Main) {
                        callback.progreso(progress)
                    }

                    val row = line.split(",").map { it.trim() }
                    if (headers != null) {
                        val map = headers.zip(row).toMap()
                        if (map.size == headers.size)
                            csvData.add(map)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading CSV file: ${e.message}", e)
            } finally {
                inputStream?.close()
                reader.close()

                withContext(Dispatchers.Main) {
                    callback.onPelosReceived(csvData)
                }
            }
        }
    }
}