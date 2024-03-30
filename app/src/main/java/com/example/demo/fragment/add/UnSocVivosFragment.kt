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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocVivosBinding
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnSocVivosFragment(unSoc: UnidSocial) : Fragment() {

    private var _binding: FragmentUnsocVivosBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: UnSocViewModel

    private val unSoc = unSoc

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[UnSocViewModel::class.java]

        binding.confirmarUnsoc.setOnClickListener { crear() }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun crear() {

        // ----- dominante ----- //
        unSoc.vAlfaS4Ad = binding.vMachoAdS4.text.toString().toInt()
        unSoc.vAlfaOtrosSA = binding.vMachoAdS4.text.toString().toInt()
        // ----- hembras y crias ----- //
        unSoc.vHembrasAd = binding.vHembrasAd.text.toString().toInt()
        unSoc.vCrias = binding.vCrias.text.toString().toInt()
        unSoc.vDestetados = binding.vDestetados.text.toString().toInt()
        unSoc.vJuveniles = binding.vJuveniles.text.toString().toInt()
        // ----- Ad/SA proximos ----- //
        unSoc.vS4AdPerif = binding.vS4AdPerif.text.toString().toInt()
        unSoc.vS4AdCerca = binding.vS4AdCerca.text.toString().toInt()
        unSoc.vS4AdLejos = binding.vS4AdLejos.text.toString().toInt()
        unSoc.vOtrosSAPerif = binding.vOtroSAPerif.text.toString().toInt()
        unSoc.vOtrosSACerca = binding.vOtroSACerca.text.toString().toInt()
        unSoc.vOtrosSALejos = binding.vOtroSALejos.text.toString().toInt()

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val unSocBD = unSoc.id?.let { model.readUnico(it) }
                if(unSocBD == null)
                    model.insert(unSoc)
                else
                    model.update(unSoc)
            }
        }

        Toast.makeText(activity, "Unidad social agregada correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.unsoc_list_fragment)
    }
}
