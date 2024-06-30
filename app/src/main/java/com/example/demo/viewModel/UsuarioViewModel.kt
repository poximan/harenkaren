package com.example.demo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.R
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.exception.MultipleUsuarioExcepcion
import com.example.demo.exception.NoExisteUsuarioExcepcion
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

        val context = super.getApplication<Application>().applicationContext
        try {
            repository.login(email, password) { usuario ->
                callback.onLoginSuccess(usuario)
            }
        } catch (e: NoExisteUsuarioExcepcion) {
            callback.onLoginFailure(context.getString(R.string.usr_VMLoginNoExiste))
        } catch (e: MultipleUsuarioExcepcion) {
            callback.onLoginFailure(context.getString(R.string.usr_VMLoginMultiple))
        } catch (e: IllegalStateException) {
            callback.onLoginFailure(context.getString(R.string.usr_VMLoginEsquema))
        }
    }

    fun crearConEmailPass(email: String, password: String, callback: UsuarioCallback) {
        repository.crearUsuario(email, password) { usuario ->
            callback.onLoginSuccess(usuario)
        }
    }
}

