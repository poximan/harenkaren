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
import kotlinx.coroutines.withContext

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

    fun readSumDia(idContInst: Int): UnidSocial {
        return repository.readSumUnSocByDiaId(idContInst)
    }

    fun readSumTotal(): UnidSocial {
        return repository.readSumTotal()
    }

    fun readAsynConFK(idRecorr: Int, callback: (List<UnidSocial>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.readAsynConFK(idRecorr)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }
}

