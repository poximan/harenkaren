package com.example.demo.fragment.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentHomeBinding
import kotlin.system.exitProcess

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.censosButton.setOnClickListener { gotoCensos() }
        binding.mapaButton.setOnClickListener { gotoMapas() }
        binding.reportesButton.setOnClickListener { goToReportes() }
        binding.ayudaButton.setOnClickListener { goToAyuda() }
        binding.desarrolloButton.setOnClickListener { gotoDesarrollo() }
        binding.logOutButton.setOnClickListener { logOut() }

        return binding.root
    }

    private fun gotoCensos() {
        findNavController().navigate(R.id.goToDiaAction)
    }

    private fun gotoMapas() {
        findNavController().navigate(R.id.goToMapaAction)
    }

    private fun goToReportes() {
        findNavController().navigate(R.id.goToRepAction)
    }

    private fun goToAyuda() {
        findNavController().navigate(R.id.goToAyudaAction)
    }

    private fun gotoDesarrollo() {
        findNavController().navigate(R.id.goToDevAction)
    }

    private fun logOut() {

        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Salir")
        builder.setMessage("¿Confirma salir?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener { _, _ ->
            exitProcess(0)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
        var alert: AlertDialog = builder.create()
        alert.show()
    }
}