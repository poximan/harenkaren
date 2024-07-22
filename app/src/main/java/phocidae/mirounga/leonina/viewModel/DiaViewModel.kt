package phocidae.mirounga.leonina.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.model.Dia
import phocidae.mirounga.leonina.repository.DiaRepository

class DiaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DiaRepository
    private val allDia: LiveData<List<Dia>>

    init {
        val diaDAO = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).diaDao()
        repository = DiaRepository(diaDAO)
        allDia = repository.diaListAll
    }

    fun insert(dia: Dia) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(dia)
    }

    fun update(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(dia)
    }

    fun delete(dia: Dia, callback: () -> Unit) = viewModelScope.launch {
        CoroutineScope(Dispatchers.IO).launch {
            repository.delete(dia)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getAnios(callback: (List<Int>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getAnios()
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    fun getDias(anio: Int, callback: (List<Dia>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getDias(anio)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }
}

