package com.example.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentCensoBinding
import com.example.demo.viewModel.DiaViewModel
import java.util.Calendar

class CensoFragment : Fragment() {

    private var _binding: FragmentCensoBinding? = null
    private val binding get() = _binding!!

    private val diaViewModel: DiaViewModel by navGraphViewModels(R.id.navHome)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCensoBinding.inflate(inflater, container, false)

        getAnios()
        return binding.root
    }

    private fun getAnios() {
        diaViewModel.getAnios {

            // crear nueva lista que sea mutable para poder agregar elementos si esta vacia
            val anios = it.toMutableList()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            if (anios.isEmpty() || !anios.contains(currentYear))
                anios.add(currentYear)

            for (anio in anios) {
                val button = Button(requireContext())
                button.text = anio.toString()

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.TOP)
                    setMargins(8, 8, 8, 8)
                }
                button.setOnClickListener {
                    gotoDia(anio)
                }
                button.layoutParams = params
                binding.gridLayout.addView(button)
            }
        }
    }

    private fun gotoDia(anio: Int) {
        val action = CensoFragmentDirections.goToDiaAction(anio)
        findNavController().navigate(action)
    }
}