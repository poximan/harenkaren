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
import com.example.demo.databinding.FragmentRecorrDetailBinding

class RecorrDetailFragment : Fragment() {

    private var _binding: FragmentRecorrDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrDetailFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorrDetailBinding.inflate(inflater, container, false)

        _binding!!.textViewObserv.text = args.recorrActual.observador
        _binding!!.textViewMeteo.text = args.recorrActual.meteo
        _binding!!.textViewAreaObs.text = args.recorrActual.areaRecorrida

        _binding!!.doneButton.setOnClickListener { goBack() }
        _binding!!.verUnSocButton.setOnClickListener { verUnidadSocial() }

        return binding.root
    }

    private fun verUnidadSocial() {
        val action = RecorrDetailFragmentDirections.goToUnSocListFromRecorrDetailAction(args.recorrActual.id)
        findNavController().navigate(action)
    }

    private fun goBack() {
        findNavController().navigate(R.id.goToRecorrListAction)
    }
}