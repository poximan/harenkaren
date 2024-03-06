package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.demo.model.Censo
import com.example.demo.database.ReportRoomDatabase
import com.example.demo.repository.ReportsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CensoViewModel(application: Application) : AndroidViewModel(application) {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String){
        _title.value = title
    }

    private val _fishingType = MutableLiveData<String>()
    val fishingType: LiveData<String>
        get() = _fishingType

    fun setFishingType(fishingType: String){
        _fishingType.value = fishingType
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

    private val repository: ReportsRepository
    val allCensos: LiveData<List<Censo>>
    init {
        val reportsDao = ReportRoomDatabase
            .getDatabase(application, viewModelScope).reportDao()
        repository = ReportsRepository(reportsDao)
        allCensos = repository.allReports
    }
    fun insert(censo: Censo) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(censo)
    }

    fun update(censo: Censo) = CoroutineScope(Dispatchers.IO).launch {
            repository.update(censo)
    }
}

