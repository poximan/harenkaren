package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.demo.database.CircuitoRoomDatabase
import com.example.demo.model.Circuito
import com.example.demo.repository.CircuitosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CircuitoViewModel(application: Application) : AndroidViewModel(application) {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String){
        _title.value = title
    }

    private val _circuito = MutableLiveData<String>()
    val fishingType: LiveData<String>
        get() = _circuito

    fun setFishingType(circuito: String){
        _circuito.value = circuito
    }

    private val _specie = MutableLiveData<String>()
    val specie: LiveData<String>
        get() = _specie

    fun setSpecie(specie: String){
        _specie.value = specie
    }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    fun setDate(date: String){
        _date.value = date
    }

    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double>
        get() = _latitude

    fun setLatitude(latitude: Double) {
        _latitude.value = latitude
    }

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double>
        get() = _longitude

    fun setLongitude(longitude: Double) {
        _longitude.value = longitude
    }

    val repository: CircuitosRepository
    val allCircuitos: LiveData<List<Circuito>>
    init {
        val reportsDao = CircuitoRoomDatabase
            .getDatabase(application, viewModelScope).cicuitoDao()
        repository = CircuitosRepository(reportsDao)
        allCircuitos = repository.allCircuitos
    }
    fun insertCircuito(censo: Circuito) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insertCircuito(censo)
    }

    fun updateCircuito(censo: Circuito) = CoroutineScope(Dispatchers.IO).launch {
            repository.updateCircuito(censo)
        }

}

