package com.example.demo.fragment.add

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocVivosBinding
import com.example.demo.viewModel.UnSocShareViewModel
import kotlin.reflect.KFunction2

class UnSocVivosFragment(private val context: Context) : Fragment() {

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
        return UnSocVivosFragment(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocVivosBinding.inflate(inflater, container, false)

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

        colectar(1, map)
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
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        sharedViewModel.lastSelectedValue.observe(viewLifecycleOwner) { ctxElegido ->

            when (ctxElegido) {
                ctxSocial[1] -> vistaHaren()
                ctxSocial[2] -> vistaHarenSinAlfa()
                ctxSocial[3] -> vistaGrupoHarenes()
                ctxSocial[4] -> vistaPjaSolitaria()
            }
        }
    }

    private fun vistaHaren() {
        when (binding.root.findFocus()?.id) {
            R.id.vAlfaS4Ad -> {
                validarDominante(
                    binding.vAlfaS4Ad,
                    binding.vAlfaSams
                )
            }

            R.id.vAlfaSams -> {
                validarDominante(
                    binding.vAlfaSams,
                    binding.vAlfaS4Ad
                )
            }

            R.id.vHembrasAd -> {
                if (!binding.vHembrasAd.text.isNullOrEmpty() &&
                    binding.vHembrasAd.text.toString().toInt() < 2
                )
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.socv_vistaHaren),
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    private fun vistaHarenSinAlfa() {

        var contMacho = 0

        if (!binding.vAlfaS4Ad.text.isNullOrEmpty())
            contMacho += binding.vAlfaS4Ad.text.toString().toInt()
        if (!binding.vAlfaSams.text.isNullOrEmpty())
            contMacho += binding.vAlfaSams.text.toString().toInt()

        if (contMacho > 0)
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.socv_vistaHarenSinAlfa1),
                Toast.LENGTH_LONG
            ).show()

        forzarValor("0", binding.vAlfaS4Ad)
        forzarValor("0", binding.vAlfaSams)

        if (!binding.vHembrasAd.text.isNullOrEmpty() &&
            binding.vHembrasAd.text.toString().toInt() < 2
        )
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.socv_vistaHarenSinAlfa2),
                Toast.LENGTH_LONG
            ).show()
    }

    private fun vistaGrupoHarenes() {

        var contMacho = 0

        if (!binding.vAlfaS4Ad.text.isNullOrEmpty())
            contMacho += binding.vAlfaS4Ad.text.toString().toInt()
        if (!binding.vAlfaSams.text.isNullOrEmpty())
            contMacho += binding.vAlfaSams.text.toString().toInt()

        if (contMacho < 2)
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.socv_vistaGrupoHarenes1),
                Toast.LENGTH_LONG
            ).show()

        if (!binding.vHembrasAd.text.isNullOrEmpty() &&
            binding.vHembrasAd.text.toString().toInt() < 2
        )
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.socv_vistaGrupoHarenes2),
                Toast.LENGTH_LONG
            ).show()
    }

    private fun vistaPjaSolitaria() {

        when (binding.root.findFocus()?.id) {
            R.id.vAlfaS4Ad -> {
                validarDominante(
                    binding.vAlfaS4Ad,
                    binding.vAlfaSams
                )
            }

            R.id.vAlfaSams -> {
                validarDominante(
                    binding.vAlfaSams,
                    binding.vAlfaS4Ad
                )
            }

            R.id.vHembrasAd -> {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.socv_vistaPjaSolitaria),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        // para pareja solitaria, siempre hembra==1
        forzarValor("1", binding.vHembrasAd)
    }

    private fun validarDominante(
        editTextPrimario: EditText,
        editTextSecundario: EditText
    ) {
        var texto = ""
        try {   // si usuario borra antes de ingresar un nuevo numero, entonces campo==""
            if (editTextPrimario.text.toString().toInt() > 1)
                texto = requireContext().getString(R.string.socv_validarDominante1)

            if (editTextPrimario.text.toString().toInt() == 1 && (
                        !editTextSecundario.text.isNullOrEmpty() && editTextSecundario.text.toString()
                            .toInt() > 0)
            )
                texto = requireContext().getString(R.string.socv_validarDominante2)
        } catch (e: NumberFormatException) {
        }

        if (texto.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                texto,
                Toast.LENGTH_LONG
            ).show()

            forzarValor("0", editTextPrimario)
        }
    }

    private fun forzarValor(valor: String, categoria: EditText) {
        categoria.removeTextChangedListener(textWatcher)
        categoria.text = valor.toEditable()
        categoria.addTextChangedListener(textWatcher)
    }

    override fun toString(): String {
        return context.getString(R.string.socv_toString)
    }
}
