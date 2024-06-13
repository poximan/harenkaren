package com.example.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentHomeBinding

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

        return binding.root
    }

    private fun gotoCensos() {
        val action = HomeFragmentDirections.goToDiaAction()
        findNavController().navigate(action)
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
}