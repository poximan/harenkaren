package com.example.demo.fragment.add

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentUnsocVivosBinding
import kotlin.reflect.KFunction2

class UnSocVivosFragment() : Fragment() {

    companion object {
        private lateinit var colectar: (Int, Map<String, Any>) -> Unit
    }
    private val map: MutableMap<String, Any> = mutableMapOf()

    private var _binding: FragmentUnsocVivosBinding? = null
    private val binding get() = _binding!!

    fun newInstance(funcColectar: KFunction2<Int, Map<String, Any>, Unit>): UnSocVivosFragment {
        colectar = funcColectar
        return UnSocVivosFragment()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)

        binding.vAlfaS4Ad.addTextChangedListener(textWatcher)
        binding.vAlfaOtrosSA.addTextChangedListener(textWatcher)
        binding.vHembrasAd.addTextChangedListener(textWatcher)
        binding.vCrias.addTextChangedListener(textWatcher)
        binding.vDestetados.addTextChangedListener(textWatcher)
        binding.vJuveniles.addTextChangedListener(textWatcher)
        binding.vS4AdPerif.addTextChangedListener(textWatcher)
        binding.vS4AdCerca.addTextChangedListener(textWatcher)
        binding.vS4AdLejos.addTextChangedListener(textWatcher)
        binding.vOtroSAPerif.addTextChangedListener(textWatcher)
        binding.vOtroSACerca.addTextChangedListener(textWatcher)
        binding.vOtroSALejos.addTextChangedListener(textWatcher)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        cargarMap()
    }

    override fun onPause() {
        super.onPause()
        cargarMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarMap() {
        // ----- dominante ----- //
        map["v_alfa_s4ad"] = safeStringToInt(binding.vAlfaS4Ad.text.toString())
        map["v_alfa_otros_sa"] = safeStringToInt(binding.vAlfaOtrosSA.text.toString())

        // ----- hembras y crias ----- //
        map["v_hembras_ad"] = safeStringToInt(binding.vHembrasAd.text.toString())
        map["v_crias"] = safeStringToInt(binding.vCrias.text.toString())
        map["v_destetados"] = safeStringToInt(binding.vDestetados.text.toString())
        map["v_juveniles"] = safeStringToInt(binding.vJuveniles.text.toString())

        // ----- Ad/SA proximos ----- //
        map["v_s4ad_perif"] = safeStringToInt(binding.vS4AdPerif.text.toString())
        map["v_s4ad_cerca"] = safeStringToInt(binding.vS4AdCerca.text.toString())
        map["v_s4ad_lejos"] = safeStringToInt(binding.vS4AdLejos.text.toString())
        map["v_otros_sa_perif"] = safeStringToInt(binding.vOtroSAPerif.text.toString())
        map["v_otros_sa_cerca"] = safeStringToInt(binding.vOtroSACerca.text.toString())
        map["v_otros_sa_lejos"] = safeStringToInt(binding.vOtroSALejos.text.toString())

        colectar(1,map)
    }

    private fun safeStringToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            cargarMap()
        }
    }

    override fun toString(): String {
        return "Vivos"
    }
}
