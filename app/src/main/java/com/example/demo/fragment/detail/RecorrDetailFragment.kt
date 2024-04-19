package com.example.demo.fragment.detail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentRecorrDetailBinding
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.model.LatLong
import com.example.demo.viewModel.RecorrViewModel
import java.text.SimpleDateFormat
import java.util.Date

class RecorrDetailFragment : Fragment() {

    private var _binding: FragmentRecorrDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrDetailFragmentArgs by navArgs()

    private lateinit var model: RecorrViewModel

    private lateinit var locationManager: LocationManager
    private var indicatorLightIni: ImageView? = null
    private var indicatorLightFin: ImageView? = null
    private val latLonIni = LatLong()
    private val latLonFin = LatLong()

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorrDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[RecorrViewModel::class.java]

        val estadosMarea = resources.getStringArray(R.array.estado_marea)
        val mareasArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, estadosMarea)
        binding.spinnerMarea.adapter = mareasArrayAdapter

        binding.idRecorr.text = "ID unico de recorrido = " + args.recorrActual.id.toString().toEditable()

        binding.editObservador.text = args.recorrActual.observador.toEditable()
        binding.areaRecorr.text = args.recorrActual.areaRecorrida.toEditable()
        binding.meteo.text = args.recorrActual.meteo.toEditable()

        val indice = obtenerPosicionSpinner(args.recorrActual.marea, binding.spinnerMarea)
        binding.spinnerMarea.setSelection(indice)
        binding.spinnerMarea.isEnabled = false

        binding.fechaIni.text = "Fecha inicio: " + args.recorrActual.fechaIni
        binding.fechaFin.text = "Fecha fin: " + args.recorrActual.fechaFin

        latLonIni.lat = args.recorrActual.latitudIni!!
        latLonIni.lon = args.recorrActual.longitudIni!!
        binding.latitudIni.text = String.format("%.6f", latLonIni.lat)
        binding.longitudIni.text = String.format("%.6f", latLonIni.lon)

        latLonFin.lat = args.recorrActual.latitudFin!!
        latLonFin.lon = args.recorrActual.longitudFin!!
        binding.latitudFin.text = String.format("%.6f", latLonFin.lat)
        binding.longitudFin.text = String.format("%.6f", latLonFin.lon)

        binding.volverButton.setOnClickListener { goBack() }
        binding.verUnSocButton.setOnClickListener { verUnidadSocial() }
        binding.confirmarButton.setOnClickListener { guardarCambios() }

        binding.getPosicionIni.setOnClickListener { indicatorLightIni?.let { it1 ->
            getPosicionActual(binding.latitudIni, binding.longitudIni, latLonIni,
                it1
            )
        } }
        binding.getPosicionFin.setOnClickListener { indicatorLightFin?.let { it1 ->
            getPosicionActual(binding.latitudFin, binding.longitudFin, latLonFin,
                it1
            )
        } }

        // Habilitar o deshabilitar los componentes según el estado del checkbox
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editObservador.isEnabled = isChecked
            binding.areaRecorr.isEnabled = isChecked
            binding.meteo.isEnabled = isChecked
            binding.spinnerMarea.isEnabled = isChecked

            binding.getPosicionIni.isEnabled = isChecked
            binding.getPosicionFin.isEnabled = isChecked

            binding.confirmarButton.isEnabled = isChecked
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLightIni = view.findViewById(R.id.gpsLightIni)
        indicatorLightFin = view.findViewById(R.id.gpsLightFin)
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
        args.recorrActual.marea = binding.spinnerMarea.selectedItem.toString()

        model.update(args.recorrActual)

        Toast.makeText(activity, "Recorrido modificado", Toast.LENGTH_LONG).show()

        val action = RecorrDetailFragmentDirections.goToRecorrListAction(args.recorrActual.diaId)
        findNavController().navigate(action)
    }

    private fun obtenerPosicionSpinner(entradaBuscada: String?, spinnerActual: Spinner): Int {
        val opcionesAdapter = spinnerActual.adapter // Obtener el adaptador del Spinner
        val opciones = mutableListOf<String>()
        if (opcionesAdapter != null) {
            for (i in 0 until opcionesAdapter.count) {
                opciones.add(opcionesAdapter.getItem(i).toString())
            }
        }
        return opciones.indexOfFirst { it.equals(entradaBuscada, ignoreCase = true) }
    }

    private fun verUnidadSocial() {
        val action = args.recorrActual.id?.let {
            RecorrDetailFragmentDirections.goToUnSocListFromRecorrDetailAction(
                it
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    private fun goBack() {
        val action = args.recorrActual.diaId?.let {
            RecorrDetailFragmentDirections.goToRecorrListAction(
                it
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    private fun getPosicionActual(latDestino: TextView, lonDestino: TextView, latLongDestino: LatLong, indicatorLight: ImageView) {

        locationManager = requireActivity().getSystemService(LocationManager::class.java)

        if (checkLocationPermission()) {
            indicatorLight?.setImageResource(R.drawable.indicator_off)

            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        indicatorLight?.setImageResource(R.drawable.indicator_on)
                        updateLocationViews(
                            latDestino, lonDestino,
                            location.latitude, location.longitude, latLongDestino)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        val message =
                            "GPS deshabilitado. Habilítar en Configuraciones."
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fineLocationPermission =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            val coarseLocationPermission =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun updateLocationViews(
        latDestino: TextView, lonDestino: TextView,
        latitud: Double, longitud: Double, latLonDestino : LatLong
    ) {

        latLonDestino.lat = latitud
        latLonDestino.lon = longitud

        mostrarEnPantalla(latDestino, lonDestino, latLonDestino)
    }

    private fun mostrarEnPantalla(latDestino: TextView, lonDestino: TextView, latLonDestino : LatLong) {

        val lat = String.format("%.6f", latLonDestino.lat)
        val lon = String.format("%.6f", latLonDestino.lon)

        latDestino.text = lat
        lonDestino.text = lon
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                UnSocGralFragment.DbConstants.PERMISSION_REQUEST_LOCATION
            )
        }
    }
}