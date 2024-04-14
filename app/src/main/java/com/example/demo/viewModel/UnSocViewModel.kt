package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.UnidSocial
import com.example.demo.repository.UnSocRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnSocViewModel(application: Application) : AndroidViewModel(application){

    private val repository: UnSocRepository
    private val allUnSoc: LiveData<List<UnidSocial>>

    init {
        val unSocDao = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).unSocDao()
        repository = UnSocRepository(unSocDao)

        allUnSoc = repository.unSocListAll
    }
    fun insert(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(unidSocial)
    }

    fun update(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(unidSocial)
    }

    fun readConFK(id: Int): LiveData<List<UnidSocial>> {
        return repository.readConFK(id)
    }

    fun readUnico(idUnSoc: Int): UnidSocial {
        return repository.readUnico(idUnSoc)
    }

    fun readSumRecorr(id: Int): UnidSocial {
        return repository.readSumUnSocByRecorrId(id)
    }

    fun readSumDia(id: Int): UnidSocial {
        return repository.readSumUnSocByDiaId(id)
    }

    fun readSumTotal(): UnidSocial {
        return repository.readSumTotal()
    }
}

