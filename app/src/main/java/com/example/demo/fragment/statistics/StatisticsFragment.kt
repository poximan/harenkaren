package com.example.demo.fragment.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var pieChart: PieChart? = null

    @RequiresApi(Build.VERSION_CODES.N)
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
                        graficar(
                            it,
                            resultado.alfaS4Ad, resultado.alfaOtrosSA, resultado.hembrasAd,
                            resultado.criasVivas, resultado.criasMuertas, resultado.destetados,
                            resultado.juveniles
                        )
                    }
                } catch (e: NullPointerException) {
                    Toast.makeText(activity, "No existe unidad social, verificar {#}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun unRecorrido(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumRecorr(binding.granulOrden.text.toString().toInt())
            withContext(Dispatchers.Main) {

                view?.let {
                    graficar(
                        it,
                        resultado.alfaS4Ad, resultado.alfaOtrosSA, resultado.hembrasAd,
                        resultado.criasVivas, resultado.criasMuertas, resultado.destetados,
                        resultado.juveniles
                    )
                }
            }
        }
    }

    private fun unDia(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumDia(binding.granulOrden.text.toString().toInt())
            withContext(Dispatchers.Main) {

                view?.let {
                    graficar(
                        it,
                        resultado.alfaS4Ad, resultado.alfaOtrosSA, resultado.hembrasAd,
                        resultado.criasVivas, resultado.criasMuertas, resultado.destetados,
                        resultado.juveniles
                    )
                }
            }
        }
    }

    private fun todosLosDias(viewModel: UnSocViewModel)  {

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = viewModel.readSumTotal()
            withContext(Dispatchers.Main) {

                view?.let {
                    graficar(
                        it,
                        resultado.alfaS4Ad, resultado.alfaOtrosSA, resultado.hembrasAd,
                        resultado.criasVivas, resultado.criasMuertas, resultado.destetados,
                        resultado.juveniles
                    )
                }
            }
        }
    }

    private fun graficar(
        view: View,
        grafText01: Int, grafText02: Int, grafText03: Int,
        grafText04: Int, grafText05: Int, grafText06: Int,
        grafText07: Int
    ) {

        _binding!!.grafText01.text = grafText01.toString()
        _binding!!.grafText02.text = grafText02.toString()
        _binding!!.grafText03.text = grafText03.toString()
        _binding!!.grafText04.text = grafText04.toString()
        _binding!!.grafText05.text = grafText05.toString()
        _binding!!.grafText06.text = grafText06.toString()
        _binding!!.grafText07.text = grafText07.toString()

        pieChart = view.findViewById(R.id.piechart)

        pieChart?.clearChart()
        setData(pieChart)
        pieChart?.startAnimation()
    }

    private fun setData(pieChart: PieChart?) {

        var color = ContextCompat.getColor(requireContext(), R.color.graf_color01)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText01.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color02)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText02.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color03)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText03.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color04)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText04.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color05)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText05.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color06)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText06.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color07)
        pieChart?.addPieSlice(
            PieModel(
                _binding!!.grafText07.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
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
