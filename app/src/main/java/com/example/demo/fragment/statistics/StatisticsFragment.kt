package com.example.demo.fragment.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.model.UnidSocial

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val coloresDisponibles = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.goBackButton.setOnClickListener { goBack() }
        binding.granulOrden.setOnEnterPressedListener { tomarDatos() }
        binding.granularidad.setOnItemSelectedListener { posicion -> contextoItemElegido(posicion) }

        return binding.root
    }

    private fun contextoItemElegido(posicion: Int) {

        if (posicion in 0..2) {
            binding.granulOrden.visibility = View.VISIBLE
            binding.granulOrden.text.clear()
        } else {
            todosLosDias(UnSocViewModel(requireActivity().application))
            binding.granulOrden.visibility = View.GONE
        }
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
            val resultado = viewModel.readUnico(binding.granulOrden.text.toString().toInt())
            withContext(Dispatchers.Main) {
                try {
                    view?.let {
                        graficar(it, resultado)
                    }
                } catch (e: NullPointerException) {
                    Toast.makeText(activity, "No existe registro, verificar {#}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun unRecorrido(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumRecorr(binding.granulOrden.text.toString().toInt())
            withContext(Dispatchers.Main) {
                view?.let {
                    graficar(it, resultado)
                }
            }
        }
    }

    private fun unDia(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumDia(binding.granulOrden.text.toString().toInt())
            withContext(Dispatchers.Main) {
                view?.let {
                    graficar(it, resultado)
                }
            }
        }
    }

    private fun todosLosDias(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumTotal()
            withContext(Dispatchers.Main) {
                view?.let {
                    graficar(it, resultado)
                }
            }
        }
    }

    private fun graficar(view: View, unidSocial: UnidSocial) {

        val pieChart: PieChart = view.findViewById(R.id.piechart)
        pieChart.clearChart()

        val contadores = unidSocial.getContadores()
        for (atribString in contadores) {
            ocultarEtiquetas(atribString)
        }

        val contadoresNoNulos = unidSocial.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {
            asignarValorPorReflexion(atribString, unidSocial)
            setData2(pieChart, atribString, unidSocial)
        }

        pieChart.startAnimation()
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

    private fun setData2(pieChart: PieChart, atribString: String, unidSocial: UnidSocial) {

        val valorAtributo = unidSocial.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = (valorAtributo.get(unidSocial) as Int).toFloat()

        pieChart.addPieSlice(
            PieModel(
                valor,
                Color.parseColor(String.format("#%06X", 0xFFFFFF and siguienteColor()))
            )
        )
    }

    private fun siguienteColor(): Int {

        val colores = listOf(
            R.color.clr_v_alfa_s4ad, R.color.clr_v_alfa_sams, R.color.clr_v_hembras_ad,
            R.color.clr_v_crias, R.color.clr_v_destetados, R.color.clr_v_juveniles,
            R.color.clr_v_s4ad_perif, R.color.clr_v_s4ad_cerca, R.color.clr_v_s4ad_lejos,
            R.color.clr_v_otros_sams_perif, R.color.clr_v_otros_sams_cerca, R.color.clr_v_otros_sams_lejos,
            R.color.clr_m_alfa_s4ad, R.color.clr_m_alfa_sams, R.color.clr_m_hembras_ad,
            R.color.clr_m_crias, R.color.clr_m_destetados, R.color.clr_m_juveniles,
            R.color.clr_m_s4ad_perif, R.color.clr_m_s4ad_cerca, R.color.clr_m_s4ad_lejos,
            R.color.clr_m_otros_sams_perif, R.color.clr_m_otros_sams_cerca, R.color.clr_m_otros_sams_lejos
        )

        // Filtrar los colores que aún no han sido utilizados
        val coloresNoUtilizados = colores.filter { !coloresDisponibles.contains(it) }

        if (coloresNoUtilizados.isEmpty()) {    // si no quedan disponibles
            coloresDisponibles.clear()
            return siguienteColor() // Volver a intentar pedir colore
        }

        // Obtener un color aleatorio de los colores no utilizados
        val colorIndex = (coloresNoUtilizados.indices).random()
        val colorId = coloresNoUtilizados[colorIndex]
        coloresDisponibles.add(colorId)

        return ContextCompat.getColor(requireContext(), colorId)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun EditText.setOnEnterPressedListener(action: () -> Unit) {
        this.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                action.invoke()
                true
            } else {
                false
            }
        }
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
                binding.granulOrden.visibility = View.GONE
            }
        }
    }
}
