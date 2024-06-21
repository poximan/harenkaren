package com.example.demo.fragment.login

import com.example.demo.model.Usuario

interface UsuarioCallback {
    fun onLoginSuccess(usuario: Usuario?)
    fun onLoginFailure(errorMessage: String)
}
