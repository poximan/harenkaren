package phocidae.mirounga.leonina.fragment.edit

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentRecorrDetailBinding
import phocidae.mirounga.leonina.model.LatLong
import phocidae.mirounga.leonina.viewModel.RecorrViewModel
import java.text.SimpleDateFormat
import java.util.Date

class RecorrEditFragment : EditGralAbstract() {

    private var _binding: FragmentRecorrDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrEditFragmentArgs by navArgs()

    private var model: RecorrViewModel? = null

    private var latLonIni = LatLong()
    private var latLonFin = LatLong()

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorrDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[RecorrViewModel::class.java]

        val estadosMarea = resources.getStringArray(R.array.op_marea)
        val mareasArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, estadosMarea)
        binding.spinnerMarea.adapter = mareasArrayAdapter

        binding.editObservador.text = args.recorrActual.observador.toEditable()
        binding.areaRecorr.text = args.recorrActual.areaRecorrida.toEditable()
        binding.meteo.text = args.recorrActual.meteo.toEditable()
        binding.observaciones.text = args.recorrActual.observaciones.toEditable()

        val indice = obtenerPosicionSpinner(args.recorrActual.marea, binding.spinnerMarea)
        binding.spinnerMarea.setSelection(indice)
        binding.spinnerMarea.isEnabled = false

        binding.horaIni.text =
            "${requireContext().getString(R.string.rec_horaini)}: " + args.recorrActual.fechaIni
        binding.horaFin.text =
            "${requireContext().getString(R.string.rec_horafin)}: " + args.recorrActual.fechaFin

        indicatorLight = binding.gpsLight
        latLonIni.lat = args.recorrActual.latitudIni
        latLonIni.lon = args.recorrActual.longitudIni
        latLonFin.lat = args.recorrActual.latitudFin
        latLonFin.lon = args.recorrActual.longitudFin

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getPosicionFin.setOnClickListener { getPosicionActual(binding.latitudFin) }
        binding.volverButton.setOnClickListener { goBack() }
        binding.verUnSocButton.setOnClickListener { verUnidadSocial() }
        binding.confirmarButton.setOnClickListener { guardarCambios() }

        // Habilitar o deshabilitar los componentes según el estado del checkbox
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editObservador.isEnabled = isChecked
            binding.areaRecorr.isEnabled = isChecked
            binding.meteo.isEnabled = isChecked
            binding.observaciones.isEnabled = isChecked
            binding.spinnerMarea.isEnabled = isChecked
            binding.getPosicionFin.isEnabled = isChecked
            binding.confirmarButton.isEnabled = isChecked
        }
        mostrarEnPantalla()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        model = null
        latLonIni = LatLong()
        latLonFin = LatLong()
    }

    private fun guardarCambios() {

        val formato = requireContext().resources.getString(R.string.formato_fecha)

        args.recorrActual.observador = binding.editObservador.text.toString()
        args.recorrActual.fechaFin = SimpleDateFormat(formato).format(Date())

        args.recorrActual.latitudIni = latLonIni.lat
        args.recorrActual.longitudIni = latLonIni.lon
        args.recorrActual.latitudFin = latLonFin.lat
        args.recorrActual.longitudFin = latLonFin.lon

        args.recorrActual.areaRecorrida = binding.areaRecorr.text.toString()
        args.recorrActual.meteo = binding.meteo.text.toString()
        args.recorrActual.observaciones = binding.observaciones.text.toString()
        args.recorrActual.marea = binding.spinnerMarea.selectedItem.toString()

        model!!.update(args.recorrActual)

        val context = requireContext()
        Toast.makeText(context, context.getString(R.string.rec_confirmarEdit), Toast.LENGTH_LONG)
            .show()

        val action = RecorrEditFragmentDirections.goToRecorrListAction(args.recorrActual.diaId)
        findNavController().navigate(action)
    }

    private fun verUnidadSocial() {
        val action =
            RecorrEditFragmentDirections.goToUnSocListFromRecorrDetailAction(args.recorrActual.id)
        findNavController().navigate(action)
    }

    private fun goBack() {
        val action = RecorrEditFragmentDirections.goToRecorrListAction(args.recorrActual.diaId)
        findNavController().navigate(action)
    }

    override fun updateLocationViews(latitud: Double, longitud: Double) {

        latLonFin.lat = latitud
        latLonFin.lon = longitud
        mostrarEnPantalla()
    }

    private fun mostrarEnPantalla() {

        binding.latitudIni.text = String.format("%.6f", latLonIni.lat)
        binding.longitudIni.text = String.format("%.6f", latLonIni.lon)
        binding.latitudFin.text = String.format("%.6f", latLonFin.lat)
        binding.longitudFin.text = String.format("%.6f", latLonFin.lon)
    }
}