package com.example.demo.repository

import com.example.demo.dao.UsuarioDAO
import com.example.demo.exception.MultipleUsuarioException
import com.example.demo.exception.NoExisteUsuarioException
import com.example.demo.model.Usuario

class UsuarioRepository(private val dao: UsuarioDAO) {

    fun insert(elem: Usuario) {
        dao.insert(elem)
    }

    fun update(elem: Usuario) {
        dao.update(elem)
    }

    fun login(email: String, pass: String, callback: (Usuario) -> Unit) {
        val usuarioBD = dao.getUsuario(email, pass)

        if (usuarioBD.isEmpty())
            throw NoExisteUsuarioException()

        if (usuarioBD.count() > 1)
            throw MultipleUsuarioException()

        callback(usuarioBD.first())
    }

    fun crearUsuario(email: String, pass: String, callback: (Usuario) -> Unit) {
        val idNuevo = dao.create(email, pass)
        callback(dao.getUsuarioById(idNuevo.toInt()))
    }
}