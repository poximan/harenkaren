package com.example.demo.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.database.DevFragment
import com.example.demo.databinding.FragmentRecorrAddBinding
import com.example.demo.exception.CamposVaciosExcepcion
import com.example.demo.exception.FaltaLatLongExcepcion
import com.example.demo.model.LatLong
import com.example.demo.model.Recorrido
import com.example.demo.viewModel.RecorrViewModel
import java.text.SimpleDateFormat
import java.util.Date

class RecorrAddFragment : AddGralAbstract() {

    private var _binding: FragmentRecorrAddBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrAddFragmentArgs by navArgs()

    private var model: RecorrViewModel? = null
    private var latLonIni = LatLong()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecorrAddBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[RecorrViewModel::class.java]

        val estadosMarea = resources.getStringArray(R.array.op_marea)
        val mareasArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, estadosMarea)
        binding.spinnerMarea.adapter = mareasArrayAdapter

        binding.getPosicionIni.setOnClickListener { getPosicionActual(binding.latitudIni) }
        binding.confirmarRecorridoButton.setOnClickListener { confirmarRecorrido() }

        return view
    }

    private fun confirmarRecorrido() {

        val context = requireContext()
        var justificacion = ""

        try {
            val recorrido = dataDesdeIU()
            model!!.insert(recorrido)

            justificacion = context.getString(R.string.rec_confirmar)
            val action = RecorrAddFragmentDirections.goToRecorrListAction(args.idDia)
            findNavController().navigate(action)
        } catch (e: CamposVaciosExcepcion) {
            justificacion =
                context.getString(R.string.soc_confirmarFalla) + ": " + e.message.toString()
        }
        Toast.makeText(context, justificacion, Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = binding.gpsLightRecorr
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        model = null
        latLonIni = LatLong()
    }

    private fun dataDesdeIU(): Recorrido {

        val uuid = DevFragment.UUID_NULO
        val observador = binding.editObservador.text.toString()
        val areaRecorrida = binding.areaRecorr.text.toString()
        val meteo = binding.editTextMeteo.text.toString()
        val marea = binding.spinnerMarea.selectedItem.toString()
        val observaciones = binding.observaciones.text.toString()

        if (latLonIni.lat == 0.0 || latLonIni.lon == 0.0)
            throw FaltaLatLongExcepcion(requireContext().getString(R.string.varias_validarGPS))

        val formato = requireContext().resources.getString(R.string.formato_fecha)
        val fechaIni = SimpleDateFormat(formato).format(Date())

        return Recorrido(
            uuid, args.idDia, observador, fechaIni, "",
            latLonIni.lat, latLonIni.lon, 0.0, 0.0,
            areaRecorrida, meteo, marea, observaciones
        )
    }

    override fun updateLocationViews(latitud: Double, longitud: Double) {

        latLonIni.lat = latitud
        latLonIni.lon = longitud

        mostrarEnPantalla()
    }

    private fun mostrarEnPantalla() {

        val lat = String.format("%.6f", latLonIni.lat)
        val lon = String.format("%.6f", latLonIni.lon)

        binding.latitudIni.text = lat
        binding.longitudIni.text = lon
    }
}
