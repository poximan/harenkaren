package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.Recorrido
import com.example.demo.repository.RecorrRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecorrViewModel(application: Application) : AndroidViewModel(application) {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val repository: RecorrRepository

    val recorrList: LiveData<List<Recorrido>>

    init {
        val circuitosDAO = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).recorrDao()
        repository = RecorrRepository(circuitosDAO)
        recorrList = repository.recorrList
    }

    fun insert(recorrido: Recorrido) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(recorrido)
    }

    fun update(recorrido: Recorrido) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(recorrido)
    }
}

