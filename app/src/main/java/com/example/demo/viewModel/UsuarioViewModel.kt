package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.exception.MultipleUsuarioException
import com.example.demo.exception.NoExiteUsuarioException
import com.example.demo.fragment.login.UsuarioCallback
import com.example.demo.model.Dia
import com.example.demo.repository.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository

    init {
        val dao = HarenKarenRoomDatabase
            .getDatabase(application, viewModelScope).usuarioDao()
        repository = UsuarioRepository(dao)
    }

    fun insert(dia: Dia) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(dia)
    }

    fun update(dia: Dia) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(dia)
    }

    fun loginConEmailPass(email: String, password: String, callback: UsuarioCallback) {
        try {
            repository.login(email, password)
            callback.onLoginSuccess()
        }
        catch (e: NoExiteUsuarioException){
            callback.onLoginFailure("No se encontraron coincidencias para {email, contraseña}")
        }
        catch (e: MultipleUsuarioException){
            callback.onLoginFailure("Inconsistencia BD, instancia multiple para {email, contraseña}")
        }
    }

    fun crearConEmailPass(email: String, password: String, callback: UsuarioCallback) {
        repository.crearUsuario(email, password)
        callback.onLoginSuccess()
    }
}

