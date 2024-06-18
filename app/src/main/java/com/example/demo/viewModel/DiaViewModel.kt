package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.repository.DiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

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

