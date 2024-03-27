package com.example.demo.repository

import com.example.demo.dao.UsuarioDAO
import com.example.demo.exception.MultipleUsuarioException
import com.example.demo.exception.NoExiteUsuarioException
import com.example.demo.model.Dia
import com.example.demo.model.Usuario


class UsuarioRepository(private val dao: UsuarioDAO) {

    fun insert(elem: Dia) {
        dao.insert(elem)
    }
    fun update(elem: Dia) {
        dao.update(elem)
    }

    fun login(email: String, pass: String): Usuario {
        val usuarioBD = dao.getUsuario(email, pass)

        if(usuarioBD.isEmpty())
            throw NoExiteUsuarioException()

        if(usuarioBD.count() > 1)
            throw MultipleUsuarioException()

        return usuarioBD.first()
    }

    fun crearUsuario(email: String, pass: String): Usuario {
        val idNuevo = dao.create(email, pass)
        return dao.getUsuarioById(idNuevo.toInt())
    }
}