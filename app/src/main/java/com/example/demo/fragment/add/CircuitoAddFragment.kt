package com.example.demo.fragment.add

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentCircuitoAddBinding
import com.example.demo.model.Circuito
import com.example.demo.viewModel.CircuitoViewModel
import java.util.Date

class CircuitoAddFragment : Fragment() {

    private var _binding: FragmentCircuitoAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: CircuitoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCircuitoAddBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[CircuitoViewModel::class.java]
        binding.confirmarCircuitoButton.setOnClickListener { confirmarCircuito() }
        return view
    }

    private fun confirmarCircuito() {

        val circuito = dataDesdeIU()
        model.insertCircuito(circuito)

        Toast.makeText(activity, "Circuito agregado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.mis_circuitos_fragment)
    }

    private fun dataDesdeIU(): Circuito {
        val observador = binding.editTextObservador.text.toString()
        val areaRecorrida = binding.editTextAreaRecorrida.text.toString()
        val meteo = binding.editTextMeteo.text.toString()
        val lat = binding.latitud.text.toString().toDouble()
        val lon = binding.longitud.text.toString().toDouble()
        return Circuito(0,observador, "19/28",lat,lon,1.0,2.0,areaRecorrida,meteo)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

