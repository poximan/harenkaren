package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.model.UnidSocial
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.repository.UnSocRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnSocViewModel(application: Application) : AndroidViewModel(application) {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val repository: UnSocRepository

    val allUnSoc: LiveData<List<UnidSocial>>

    init {
        val reportsDao = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).unSocDao()
        repository = UnSocRepository(reportsDao)

        allUnSoc = repository.unSocListAll
    }
    fun insert(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(unidSocial)
    }

    fun update(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(unidSocial)
    }

    fun read(idRecorrido: Int) : LiveData<List<UnidSocial>> {
        return repository.read(idRecorrido)
    }
}

