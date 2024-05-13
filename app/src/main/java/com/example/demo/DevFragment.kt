package com.example.demo

import android.os.Bundle
import android.view.LayoutInflater
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
import java.util.UUID

class DevFragment : Fragment() {

    private var _binding: FragmentDevBinding? = null
    private val binding get() = _binding!!

    companion object {
        var UUID_NULO: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDevBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.poblarBd.setOnClickListener { poblar() }
        binding.usuario.setOnClickListener { altaUsuario() }

        binding.limpiarDias.setOnClickListener { limpiarDias() }
        binding.limpiarRecorr.setOnClickListener { limpiarRecorr() }
        binding.limpiarUnsoc.setOnClickListener { limpiarUnidSoc() }

        estadoBD()
        return binding.root
    }

    private fun estadoBD() {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background

                val bd = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)

                val dias = bd.diaDao().getCount()
                val recorr = bd.recorrDao().getCount()
                val unsoc = bd.unSocDao().getCount()
                withContext(Dispatchers.Main) {
                    binding.dias.text = "$dias entidades Dia"
                    binding.recorr.text = "$recorr entidades Recorrido"
                    binding.unsoc.text = "$unsoc entidades Unid.Social"
                }
            }
        }
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

                val listDias: Array<UUID> =
                    datos.generarDias(diaDao).filterNotNull().toTypedArray()
                val listRecorr: Array<UUID> =
                    datos.generarRecorridos(recorrDao, listDias).filterNotNull().toTypedArray()
                datos.generarUnidadesSociales(unSocDao, listRecorr)

                withContext(Dispatchers.Main) {
                    estadoBD()
                }
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

                withContext(Dispatchers.Main) {
                    estadoBD()
                }
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
        estadoBD()
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
        estadoBD()
    }
}