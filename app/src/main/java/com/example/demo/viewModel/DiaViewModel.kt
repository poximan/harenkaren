package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.Dia
import com.example.demo.repository.DiaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaViewModel(application: Application) : AndroidViewModel(application) {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val repository: DiaRepository

    val diaList: LiveData<List<Dia>>

    init {
        val diaDAO = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).diaDao()
        repository = DiaRepository(diaDAO)
        diaList = repository.diaList
    }

    fun insert(dia: Dia) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(dia)
    }

    fun update(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(dia)
    }
}

