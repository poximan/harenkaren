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
import com.example.demo.databinding.FragmentDiaAddBinding
import com.example.demo.model.Dia
import com.example.demo.viewModel.DiaViewModel
import java.text.SimpleDateFormat
import java.util.Date

class DiaAddFragment : Fragment() {

    private var _binding: FragmentDiaAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: DiaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDiaAddBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[DiaViewModel::class.java]

        binding.confirmarDiaButton.setOnClickListener { confirmarDia() }
        return view
    }

    private fun confirmarDia() {

        val dia = dataDesdeIU()
        model.insert(dia)

        Toast.makeText(activity, "Dia agregado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.dia_list_fragment)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dataDesdeIU(): Dia {

        val timeStamp = SimpleDateFormat("yyyy/MM/dd - HH:mm:ss").format(Date())
        val meteo = binding.editTextMeteo.text.toString()

        return Dia(0, timeStamp,meteo)
    }
}

