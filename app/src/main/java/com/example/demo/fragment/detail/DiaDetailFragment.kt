package com.example.demo.fragment.detail

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentDiaDetailBinding

class DiaDetailFragment : Fragment() {

    private var _binding: FragmentDiaDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DiaDetailFragmentArgs by navArgs()

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaDetailBinding.inflate(inflater, container, false)

        binding.idDia.text = "ID unico de dia = " + args.diaActual.orden.toString().toEditable()
        val origen = args.diaActual.celularId.substringAfter("@")
        binding.origen.text = "Generado en = " + origen.toEditable()

        binding.volverButton.setOnClickListener { goBack() }
        binding.verRecorrButton.setOnClickListener { verRecorrido() }

        return binding.root
    }

    private fun verRecorrido() {
        val action =
            DiaDetailFragmentDirections.goToRecorrListFromDiaDetailAction(args.diaActual.id)
        findNavController().navigate(action)
    }

    private fun goBack() {
        findNavController().navigate(R.id.goToDiaListAction)
    }
}