package com.example.demo.fragment.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.DevFragment
import com.example.demo.R
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.UUID

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsFragmentArgs by navArgs()
    private var uuid: UUID = DevFragment.UUID_NULO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.goBackButton.setOnClickListener { goBack() }
        binding.granularidad.setOnItemSelectedListener { posicion -> contextoItemElegido(posicion) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (val entidad = args.entidad) {
            is Dia -> {
                binding.granularidad.setSelection(2)
                uuid = entidad.id
                tomarDatos()
            }
            is Recorrido -> {
                binding.granularidad.setSelection(1)
                uuid = entidad.id
                tomarDatos()
            }
            is UnidSocial -> {
                binding.granularidad.setSelection(0)
                uuid = entidad.id
                tomarDatos()
            }
            else -> { }
        }
    }

    private fun contextoItemElegido(posicion: Int) {
        if (posicion == 3)
            todosLosDias(UnSocViewModel(requireActivity().application))
    }

    private fun tomarDatos() {

        val viewModel = UnSocViewModel(requireActivity().application)

        if (binding.granularidad.selectedItemPosition == 0)
            unaUnidadSocial(viewModel)

        if (binding.granularidad.selectedItemPosition == 1)
            unRecorrido(viewModel)

        if (binding.granularidad.selectedItemPosition == 2)
            unDia(viewModel)
    }

    private fun unaUnidadSocial(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readUnico(uuid)
            withContext(Dispatchers.Main) {
                graficar(resultado)
            }
        }
    }

    private fun unRecorrido(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumRecorr(uuid)
            withContext(Dispatchers.Main) {
                graficar(resultado)
            }
        }
    }

    private fun unDia(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumDia(uuid)
            withContext(Dispatchers.Main) {
                graficar(resultado)
            }
        }
    }

    private fun todosLosDias(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumTotal()
            withContext(Dispatchers.Main) {
                graficar(resultado)
            }
        }
    }

    private fun graficar(unidSocial: UnidSocial) {

        val pieChart: PieChart = view!!.findViewById(R.id.piechart)
        pieChart.clearChart()

        val contadores = unidSocial.getContadores()
        for (atribString in contadores) {
            ocultarEtiquetas(atribString)
        }

        val contadoresNoNulos = unidSocial.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {

            asignarValorPorReflexion(atribString, unidSocial)
            if(atribIsCheck(atribString)) {
                pieChart.addPieSlice(setData(atribString, unidSocial))
            }
        }

        pieChart.startAnimation()
    }

    private fun atribIsCheck(atribString: String): Boolean {

        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "chk${capitalizar}${atribString.substring(1)}"
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        val checkBox = field.get(binding) as CheckBox

        return checkBox.isChecked
    }

    private fun ocultarEtiquetas(atribString: String) {

        // Genera el nombre del campo correspondiente al componente visual
        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "txt${capitalizar}${atribString.substring(1)}"

        // Obtiene el campo del binding utilizando reflexión
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        field.isAccessible = true

        if (field.type == TextView::class.java) {
            val textView = field.get(binding) as TextView
            val layoutExiste = textView.parent as LinearLayout
            layoutExiste.visibility = View.GONE
        }
    }

    private fun asignarValorPorReflexion(atribString: String, unidSocial: UnidSocial) {

        // Genera el nombre del campo correspondiente al componente visual
        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "txt${capitalizar}${atribString.substring(1)}"

        // Obtiene el campo del binding utilizando reflexión
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        field.isAccessible = true

        if (field.type == TextView::class.java) {
            val textView = field.get(binding) as TextView

            // obtengo un objeto Field
            val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
            valorAtributo.isAccessible = true
            // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
            val valor = valorAtributo.get(unidSocial)
            textView.text = "$atribString: $valor"

            val layoutExiste = textView.parent as LinearLayout
            layoutExiste.visibility = View.VISIBLE
        }
    }

    private fun setData(atribString: String, unidSocial: UnidSocial): PieModel {

        val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = (valorAtributo.get(unidSocial) as Int).toFloat()
        return PieModel(atribString, valor, siguienteColor(atribString))
    }

    private fun siguienteColor(atribString: String): Int {

        val coloresMap = mapOf(
            "vAlfaS4Ad" to R.color.clr_v_alfa_s4ad,
            "vAlfaSams" to R.color.clr_v_alfa_sams,
            "vHembrasAd" to R.color.clr_v_hembras_ad,
            "vCrias" to R.color.clr_v_crias,
            "vDestetados" to R.color.clr_v_destetados,
            "vJuveniles" to R.color.clr_v_juveniles,
            "vS4AdPerif" to R.color.clr_v_s4ad_perif,
            "vS4AdCerca" to R.color.clr_v_s4ad_cerca,
            "vS4AdLejos" to R.color.clr_v_s4ad_lejos,
            "vOtrosSamsPerif" to R.color.clr_v_otros_sams_perif,
            "vOtrosSamsCerca" to R.color.clr_v_otros_sams_cerca,
            "vOtrosSamsLejos" to R.color.clr_v_otros_sams_lejos,
            "mAlfaS4Ad" to R.color.clr_m_alfa_s4ad,
            "mAlfaSams" to R.color.clr_m_alfa_sams,
            "mHembrasAd" to R.color.clr_m_hembras_ad,
            "mCrias" to R.color.clr_m_crias,
            "mDestetados" to R.color.clr_m_destetados,
            "mJuveniles" to R.color.clr_m_juveniles,
            "mS4AdPerif" to R.color.clr_m_s4ad_perif,
            "mS4AdCerca" to R.color.clr_m_s4ad_cerca,
            "mS4AdLejos" to R.color.clr_m_s4ad_lejos,
            "mOtrosSamsPerif" to R.color.clr_m_otros_sams_perif,
            "mOtrosSamsCerca" to R.color.clr_m_otros_sams_cerca,
            "mOtrosSamsLejos" to R.color.clr_m_otros_sams_lejos
        )

        return ContextCompat.getColor(requireContext(), coloresMap[atribString]!!)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun Spinner.setOnItemSelectedListener(action: (position: Int) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                action.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
