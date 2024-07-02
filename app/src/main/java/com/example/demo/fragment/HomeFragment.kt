package com.example.demo.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentHomeBinding
import com.example.demo.model.Usuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.censosButton.setOnClickListener { gotoCensos() }
        binding.mapaButton.setOnClickListener { gotoMapas() }
        binding.reportesButton.setOnClickListener { goToReportes() }
        binding.ayudaButton.setOnClickListener { goToAyuda() }
        binding.desarrolloButton.setOnClickListener { gotoDesarrollo() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        se guarda en argumento una referencia del usuario que entro a la app.
        por ahora no tiene utilidad, pero parece un dato que sera necesario pronto
         */
        if (arguments == null) {
            val usuario = activity?.intent?.getSerializableExtra("usuario")
            var mensaje = ""

            mensaje = if (usuario != null) {
                usuario as Usuario
                getString(R.string.hom_onViewUsuario) + ", ${usuario.email}"
            } else {
                getString(R.string.hom_onViewHuella)
            }
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            arguments = Bundle().apply {
                putSerializable("usuario", usuario)
            }
        }
    }

    private fun gotoCensos() {
        val action = HomeFragmentDirections.goToCensoAction()
        findNavController().navigate(action)
    }

    private fun gotoMapas() {
        findNavController().navigate(R.id.goToMapaAction)
    }

    private fun goToReportes() {
        showDateRangePicker { startDate, endDate ->
            val rangoFechas = "$startDate $endDate"
            val action = HomeFragmentDirections.goToRepAction(rangoFechas)
            findNavController().navigate(action)
        }
    }

    private fun goToAyuda() {
        findNavController().navigate(R.id.goToAyudaAction)
    }

    private fun gotoDesarrollo() {
        findNavController().navigate(R.id.goToDevAction)
    }

    private fun showDateRangePicker(onDateSelected: (String, String) -> Unit) {
        val dateFormat = getString(R.string.formato_dia)
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Dialogo para elegir fecha de inicio
        val startDatePicker = DatePickerDialog(requireContext())

        startDatePicker.setTitle(getString(R.string.hom_fechaDesde))
        startDatePicker.setOnDateSetListener { _, startYear, startMonth, startDay ->
            val startCalendar = Calendar.getInstance().apply {
                set(startYear, startMonth, startDay)
            }
            val startDate = dateFormatter.format(startCalendar.time)

            // fecha de fin con fecha mínima establecida en funcion de inicio
            val endDatePicker =
                DatePickerDialog(requireContext(), null, startYear, startMonth, startDay)
            endDatePicker.datePicker.minDate =
                startCalendar.timeInMillis // Establecer la fecha mínima

            endDatePicker.setTitle(R.string.hom_fechaHasta)
            endDatePicker.setOnDateSetListener { _, endYear, endMonth, endDay ->
                val endCalendar = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay)
                }
                val endDate = dateFormatter.format(endCalendar.time)

                onDateSelected(startDate, endDate)
            }
            endDatePicker.show()
        }
        startDatePicker.show()
    }
}