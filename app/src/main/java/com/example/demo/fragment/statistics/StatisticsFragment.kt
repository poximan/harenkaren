package com.example.demo.fragment.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.viewModel.DiaViewModel
import com.example.demo.viewModel.RecorrViewModel
import com.example.demo.viewModel.UnSocViewModel
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

    private fun contextoItemElegido(posicion : Int) {

        if (posicion in 0..2) {
            binding.granulOrden.visibility = View.VISIBLE
            binding.granulOrden.text.clear()
        } else {
            tomarDatos()
            binding.granulOrden.visibility = View.GONE
        }
    }

    private fun tomarDatos() {

        Log.i("info", "aca tengo que tomar datos")

        val reflecViewModelClass = when (binding.granularidad.selectedItemPosition) {
            0 -> UnSocViewModel::class
            1 -> UnSocViewModel::class
            2 -> RecorrViewModel::class
            else -> RecorrViewModel::class
        }

        try {
            val constructor = reflecViewModelClass.constructors.first()
            val res = reflecViewModelClass::class.members.firstOrNull { it.name == "read" }?.call(1)

            Log.i("info", "ccontrolar que trajo")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graficar(view)
    }

    private fun graficar(view : View) {

        _binding!!.grafText01.text = Integer.toString(0)
        _binding!!.grafText02.text = Integer.toString(0)
        _binding!!.grafText03.text = Integer.toString(0)
        _binding!!.grafText04.text = Integer.toString(0)
        _binding!!.grafText05.text = Integer.toString(0)
        _binding!!.grafText06.text = Integer.toString(0)
        _binding!!.grafText07.text = Integer.toString(0)

        pieChart = view.findViewById(R.id.piechart)
        setData(pieChart)
        pieChart?.startAnimation();
    }

    private fun setData(pieChart: PieChart?) {

        var color = ContextCompat.getColor(requireContext(), R.color.graf_color01)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText01.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color02)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText02.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color03)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText03.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color04)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText04.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color05)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText05.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color06)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText06.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )

        color = ContextCompat.getColor(requireContext(), R.color.graf_color07)
        pieChart?.addPieSlice(
            PieModel(_binding!!.grafText07.text.toString().toFloat(),
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action.invoke(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.granulOrden.visibility = View.GONE
            }
        }
    }
}
