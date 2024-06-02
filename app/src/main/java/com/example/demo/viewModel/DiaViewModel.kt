package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.Dia
import com.example.demo.repository.DiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class DiaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DiaRepository
    val allDia: LiveData<List<Dia>>

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

    fun contarUnSocPorDia(id: UUID): Int {
        return repository.contarUnSocPorDia(id)
    }
}

