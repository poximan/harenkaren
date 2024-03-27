package com.example.demo.fragment.login

interface UsuarioCallback {
    fun onLoginSuccess()
    fun onLoginFailure(errorMessage: String)
}
