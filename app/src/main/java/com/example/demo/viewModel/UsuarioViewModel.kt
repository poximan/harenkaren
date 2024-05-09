package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.exception.MultipleUsuarioException
import com.example.demo.exception.NoExisteUsuarioException
import com.example.demo.fragment.login.UsuarioCallback
import com.example.demo.model.Usuario
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

    fun insert(elem: Usuario) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        repository.insert(elem)
    }

    fun update(elem: Usuario) = CoroutineScope(Dispatchers.IO).launch {
        repository.update(elem)
    }

    fun loginConEmailPass(email: String, password: String, callback: UsuarioCallback) {
        try {
            repository.login(email, password)
            callback.onLoginSuccess()
        } catch (e: NoExisteUsuarioException) {
            callback.onLoginFailure("No se encontraron coincidencias para {email, contraseña}")
        } catch (e: MultipleUsuarioException) {
            callback.onLoginFailure("Inconsistencia BD, instancia multiple para {email, contraseña}")
        }
    }

    fun crearConEmailPass(email: String, password: String, callback: UsuarioCallback) {
        repository.crearUsuario(email, password)
        callback.onLoginSuccess()
    }
}

