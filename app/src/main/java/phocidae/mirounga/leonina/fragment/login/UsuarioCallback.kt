package phocidae.mirounga.leonina.fragment.login

import phocidae.mirounga.leonina.model.Usuario

interface UsuarioCallback {
    fun onLoginSuccess(usuario: Usuario?)
    fun onLoginFailure(errorMessage: String)
}
