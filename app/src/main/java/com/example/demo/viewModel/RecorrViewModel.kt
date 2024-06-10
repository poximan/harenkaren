package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.Recorrido
import com.example.demo.repository.RecorrRepository
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
        repository.insert(recorrido)
    }

    fun update(recorrido: Recorrido) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(recorrido)
    }

    fun readConFK(idDia: UUID): List<Recorrido> {
        return repository.readConFK(idDia)
    }

    fun readAsynConFK(idDia: UUID, callback: (List<Recorrido>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.readAsynConFK(idDia)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }
}

