package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.*
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
        allUnSoc = repository.unSocList
    }
    fun insert(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(unidSocial)
    }

    fun update(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(unidSocial)
    }
}

