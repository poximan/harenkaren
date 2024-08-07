package phocidae.mirounga.leonina.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.repository.RecorrRepository
import phocidae.mirounga.leonina.repository.UnSocRepository
import java.util.UUID

class UnSocViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UnSocRepository
    private val repositoryPropagado: RecorrRepository

    init {
        val unSocDao = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).unSocDao()
        repository = UnSocRepository(unSocDao)

        val recorrDao = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).recorrDao()
        repositoryPropagado = RecorrRepository(recorrDao)
    }

    fun insert(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(super.getApplication<Application>().applicationContext, unidSocial)

        val recorrActual = repositoryPropagado.readUnico(unidSocial.recorrId)
        recorrActual.latitudFin = unidSocial.latitud
        recorrActual.longitudFin = unidSocial.longitud

        repositoryPropagado.update(recorrActual)
    }

    fun update(unidSocial: UnidSocial) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(unidSocial)
    }

    fun readConFK(id: UUID): List<UnidSocial> {
        return repository.readConFK(id, super.getApplication<Application>().applicationContext)
    }

    fun readUnico(idUnSoc: UUID): UnidSocial {
        return repository.readUnico(idUnSoc)
    }

    fun readSumDia(id: UUID): UnidSocial? {
        return repository.readSumUnSocByDiaId(id)
    }

    fun readSumRecorr(id: UUID): UnidSocial? {
        return repository.readSumUnSocByRecorrId(id)
    }

    fun readSumTotal(): UnidSocial {
        return repository.readSumTotal()
    }

    fun readAsynConFK(idRecorr: UUID, callback: (List<UnidSocial>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.readAsynConFK(idRecorr)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    fun getFechaObservada(idRecorr: UUID, callback: (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFechaObservada(idRecorr)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
}

