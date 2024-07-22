package phocidae.mirounga.leonina.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import phocidae.mirounga.leonina.dao.DiaDAO
import phocidae.mirounga.leonina.model.Dia

class DiaRepository(private val diaDao: DiaDAO) {

    val diaListAll: LiveData<List<Dia>> = diaDao.getAll()

    fun getAnios(): List<Int> {
        return diaDao.getAnios()
    }

    fun insert(elem: Dia) {
        diaDao.insertConUUID(elem)
    }

    fun update(elem: Dia) {
        diaDao.update(elem)
    }

    fun delete(dia: Dia) {
        CoroutineScope(Dispatchers.IO).launch {
            diaDao.delete(dia)
        }
    }

    fun getDias(anio: Int): List<Dia> {
        return diaDao.getDias(anio)
    }
}