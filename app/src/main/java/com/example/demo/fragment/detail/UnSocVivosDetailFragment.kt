package com.example.demo.fragment.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentUnsocVivosBinding
import com.example.demo.model.UnidSocial
import kotlin.reflect.KFunction2

class UnSocVivosDetailFragment() : Fragment() {

    companion object {
        private lateinit var colectar: (Int, Map<String, Any>) -> Unit
    }
    private val map: MutableMap<String, Any> = mutableMapOf()

    private var _binding: FragmentUnsocVivosBinding? = null
    private val binding get() = _binding!!

    private lateinit var unSocEditable: UnidSocial

    fun editInstance(
        colectarFunc: KFunction2<Int, Map<String, Any>, Unit>,
        unSoc: UnidSocial
    ): UnSocVivosDetailFragment {
        colectar = colectarFunc
        unSocEditable = unSoc
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun cargarDatos() {

        binding.vAlfaS4Ad.setText(unSocEditable.vAlfaS4Ad.toString())
        binding.vAlfaSams.setText(unSocEditable.vAlfaSams.toString())

        binding.vHembrasAd.setText(unSocEditable.vHembrasAd.toString())
        binding.vCrias.setText(unSocEditable.vCrias.toString())
        binding.vDestetados.setText(unSocEditable.vDestetados.toString())
        binding.vJuveniles.setText(unSocEditable.vJuveniles.toString())

        binding.vS4AdPerif.setText(unSocEditable.vS4AdPerif.toString())
        binding.vS4AdCerca.setText(unSocEditable.vS4AdCerca.toString())
        binding.vS4AdLejos.setText(unSocEditable.vS4AdLejos.toString())
        binding.vOtrosSamsPerif.setText(unSocEditable.vOtrosSamsPerif.toString())
        binding.vOtrosSamsCerca.setText(unSocEditable.vOtrosSamsCerca.toString())
        binding.vOtrosSamsLejos.setText(unSocEditable.vOtrosSamsLejos.toString())

        binding.vAlfaS4Ad.addTextChangedListener(textWatcher)
        binding.vAlfaSams.addTextChangedListener(textWatcher)
        binding.vHembrasAd.addTextChangedListener(textWatcher)
        binding.vCrias.addTextChangedListener(textWatcher)
        binding.vDestetados.addTextChangedListener(textWatcher)
        binding.vJuveniles.addTextChangedListener(textWatcher)
        binding.vS4AdPerif.addTextChangedListener(textWatcher)
        binding.vS4AdCerca.addTextChangedListener(textWatcher)
        binding.vS4AdLejos.addTextChangedListener(textWatcher)
        binding.vOtrosSamsPerif.addTextChangedListener(textWatcher)
        binding.vOtrosSamsCerca.addTextChangedListener(textWatcher)
        binding.vOtrosSamsLejos.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
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
        map["v_alfa_sams"] = safeStringToInt(binding.vAlfaSams.text.toString())

        // ----- hembras y crias ----- //
        map["v_hembras_ad"] = safeStringToInt(binding.vHembrasAd.text.toString())
        map["v_crias"] = safeStringToInt(binding.vCrias.text.toString())
        map["v_destetados"] = safeStringToInt(binding.vDestetados.text.toString())
        map["v_juveniles"] = safeStringToInt(binding.vJuveniles.text.toString())

        // ----- Ad/SA proximos ----- //
        map["v_s4ad_perif"] = safeStringToInt(binding.vS4AdPerif.text.toString())
        map["v_s4ad_cerca"] = safeStringToInt(binding.vS4AdCerca.text.toString())
        map["v_s4ad_lejos"] = safeStringToInt(binding.vS4AdLejos.text.toString())
        map["v_otros_sams_perif"] = safeStringToInt(binding.vOtrosSamsPerif.text.toString())
        map["v_otros_sams_cerca"] = safeStringToInt(binding.vOtrosSamsCerca.text.toString())
        map["v_otros_sams_lejos"] = safeStringToInt(binding.vOtrosSamsLejos.text.toString())

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
