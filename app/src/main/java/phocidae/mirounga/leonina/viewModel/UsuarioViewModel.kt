package phocidae.mirounga.leonina.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.exception.MultipleUsuarioExcepcion
import phocidae.mirounga.leonina.exception.NoExisteUsuarioExcepcion
import phocidae.mirounga.leonina.fragment.login.UsuarioCallback
import phocidae.mirounga.leonina.model.Usuario
import phocidae.mirounga.leonina.repository.UsuarioRepository
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

