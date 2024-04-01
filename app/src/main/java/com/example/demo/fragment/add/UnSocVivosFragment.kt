package com.example.demo.fragment.add

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentUnsocVivosBinding
import kotlin.reflect.KFunction2

class UnSocVivosFragment() : Fragment() {

    companion object {
        private lateinit var funColectar: (Int, Map<String, Any>) -> Unit
    }
    private val map: MutableMap<String, Any> = mutableMapOf()

    private var _binding: FragmentUnsocVivosBinding? = null
    private val binding get() = _binding!!

    fun newInstance(colectar: KFunction2<Int, Map<String, Any>, Unit>): UnSocVivosFragment {
        funColectar = colectar
        return UnSocVivosFragment()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)
        cargarMap()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        cargarMap()
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

        funColectar(1,map)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun safeStringToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun toString(): String {
        return "Vivos"
    }
}
