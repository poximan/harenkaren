package com.example.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.database.DevDatos
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentDevBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DevFragment : Fragment() {

    private var _binding: FragmentDevBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDevBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        _binding!!.poblarBd.setOnClickListener { poblar() }
        _binding!!.usuario.setOnClickListener { altaUsuario() }

        _binding!!.limpiarDias.setOnClickListener { limpiarDias() }
        _binding!!.limpiarRecorr.setOnClickListener { limpiarRecorr() }
        _binding!!.limpiarUnsoc.setOnClickListener { limpiarUnidSoc() }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.regulation_filter_menu, menu)
        menu.findItem(R.id.regulation_action_filter)
    }

    private fun altaUsuario() {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos()

                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .usuarioDao()

                datos.generarUsuario(dao)
            }
        }
    }

    private fun poblar() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos()

                // Obtener los DAOs
                val diaDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .diaDao()
                val recorrDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .recorrDao()
                val unSocDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .unSocDao()

                datos.generarDias(diaDao)
                datos.generarRecorridos(recorrDao)
                datos.generarUnidadesSociales(unSocDao)
            }
        }
    }

    private fun limpiarDias() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos()

                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .diaDao()

                datos.vaciarDias(dao)
            }
        }
    }

    private fun limpiarRecorr() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos()

                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .recorrDao()

                datos.vaciarRecorridos(dao)
            }
        }
    }

    private fun limpiarUnidSoc() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos()

                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .unSocDao()

                datos.vaciarUnidadesSociales(dao)
            }
        }
    }
}