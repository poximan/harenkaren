package com.example.demo.fragment.detail

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentCircuitoDetailBinding

class CircuitoDetailFragment : Fragment() {

    private var _binding: FragmentCircuitoDetailBinding? = null
    private val binding get() = _binding!!
    private val args: CircuitoDetailFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCircuitoDetailBinding.inflate(inflater, container, false)

        _binding!!.textViewObserv.text = args.currentCircuito.observador
        _binding!!.textViewMeteo.text = args.currentCircuito.meteo
        _binding!!.textViewAreaObs.text = args.currentCircuito.areaRecorrida

        _binding!!.doneButton.setOnClickListener { goBack() }
        _binding!!.verUnSocButton.setOnClickListener { verUnidadSocial() }

        return binding.root
    }

    private fun verUnidadSocial() {
        val action = CircuitoDetailFragmentDirections.goToUnSocListFromCircuitoDetailAction()
        findNavController().navigate(action)
    }

    private fun goBack() {
        findNavController().navigate(R.id.goToMisCircuitoFromCircuitoDetailAction)
    }
}