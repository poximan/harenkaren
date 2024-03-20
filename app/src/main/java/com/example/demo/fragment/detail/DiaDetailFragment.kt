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
import com.example.demo.databinding.FragmentDiaDetailBinding

class DiaDetailFragment : Fragment() {

    private var _binding: FragmentDiaDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DiaDetailFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaDetailBinding.inflate(inflater, container, false)

        _binding!!.textViewMeteo.text = args.diaActual.meteo

        _binding!!.volverButton.setOnClickListener { goBack() }
        _binding!!.verRecorrButton.setOnClickListener { verRecorrido() }

        return binding.root
    }

    private fun verRecorrido() {
        val action = DiaDetailFragmentDirections.goToRecorrListFromDiaDetailAction(args.diaActual.id)
        findNavController().navigate(action)
    }

    private fun goBack() {
        findNavController().navigate(R.id.goToDiaListAction)
    }
}