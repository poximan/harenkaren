package phocidae.mirounga.leonina.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.repository.RecorrRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RecorrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecorrRepository
    private val allRecorr: LiveData<List<Recorrido>>

    init {
        val recorrDAO = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).recorrDao()
        repository = RecorrRepository(recorrDAO)
        allRecorr = repository.recorrListAll
    }

    fun insert(recorrido: Recorrido) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(super.getApplication<Application>().applicationContext, recorrido)
    }

    fun update(recorrido: Recorrido) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(recorrido)
    }

    fun readConFK(idDia: UUID): List<Recorrido> {
        return repository.readConFK(idDia, super.getApplication<Application>().applicationContext)
    }

    fun readAsynConFK(idDia: UUID, callback: (List<Recorrido>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.readAsynConFK(idDia)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    fun getFechaObservada(idDia: UUID, callback: (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFechaObservada(idDia)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
}

