package com.example.demo.fragment.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocVivosBinding
import com.example.demo.viewModel.UnSocShareViewModel
import kotlin.reflect.KFunction2

class UnSocVivosFragment() : Fragment() {

    companion object {
        private lateinit var colectar: (Int, Map<String, Any>) -> Unit
    }
    private val map: MutableMap<String, Any> = mutableMapOf()

    private var _binding: FragmentUnsocVivosBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: UnSocShareViewModel by activityViewModels()
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun newInstance(funcColectar: KFunction2<Int, Map<String, Any>, Unit>): UnSocVivosFragment {
        colectar = funcColectar
        return UnSocVivosFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)

        binding.vAlfaS4Ad.addTextChangedListener(textWatcher)
        binding.vAlfaSams.addTextChangedListener(textWatcher)
        binding.vCrias.addTextChangedListener(textWatcher)
        binding.vDestetados.addTextChangedListener(textWatcher)
        binding.vJuveniles.addTextChangedListener(textWatcher)
        binding.vS4AdPerif.addTextChangedListener(textWatcher)
        binding.vS4AdCerca.addTextChangedListener(textWatcher)
        binding.vS4AdLejos.addTextChangedListener(textWatcher)
        binding.vOtrosSamsPerif.addTextChangedListener(textWatcher)
        binding.vOtrosSamsCerca.addTextChangedListener(textWatcher)
        binding.vOtrosSamsLejos.addTextChangedListener(textWatcher)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        observarCtxSocial()
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
            observarCtxSocial()
            cargarMap()
        }
    }

    private fun observarCtxSocial() {
        // si en la solapa general se eligio pareja solitaria
        sharedViewModel.lastSelectedValue.observe(viewLifecycleOwner) { ctxElegido ->
            vistaPjaSolitaria(ctxElegido)
            vistaGrupoHarenes(ctxElegido)
        }
    }

    private fun vistaGrupoHarenes(ctxElegido: String) {
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)

        if(ctxElegido == ctxSocial[3]) {
            var contMacho = 0

            if (!binding.vAlfaS4Ad.text.isNullOrEmpty())
                contMacho += binding.vAlfaS4Ad.text.toString().toInt()
            if (!binding.vAlfaSams.text.isNullOrEmpty())
                contMacho += binding.vAlfaSams.text.toString().toInt()

            if(contMacho < 2)
                Toast.makeText(
                    activity,
                    "Para grupo de harenes, la suma de los machos dominantes deben ser >=2",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun vistaPjaSolitaria(ctxElegido: String) {
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)

        if(ctxElegido == ctxSocial[4]) {
            binding.vHembrasAd.removeTextChangedListener(textWatcher)
            when (binding.root.findFocus()?.id) {
                R.id.vAlfaS4Ad -> {
                    if (!binding.vAlfaS4Ad.text.isNullOrEmpty() && binding.vAlfaS4Ad.text.toString()
                            .toInt() > 1
                    ) {
                        Toast.makeText(
                            activity,
                            "Solo se permite [0,1] si es pareja solitaria",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.vAlfaS4Ad.text = "1".toEditable()
                    }
                }

                R.id.vAlfaSams -> {
                    if (!binding.vAlfaSams.text.isNullOrEmpty() && binding.vAlfaSams.text.toString()
                            .toInt() > 1
                    ) {
                        Toast.makeText(
                            activity,
                            "Solo se permite [0,1] si es pareja solitaria",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.vAlfaSams.text = "1".toEditable()
                    }
                }
            }
            binding.vHembrasAd.text = "1".toEditable()
            binding.vHembrasAd.isEnabled = false
        } else {
            binding.vHembrasAd.addTextChangedListener(textWatcher)
            binding.vHembrasAd.isEnabled = true
        }
    }

    override fun toString(): String {
        return "Vivos"
    }
}
