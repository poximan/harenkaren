package com.example.demo.fragment.add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentRecorrAddBinding
import com.example.demo.model.LatLong
import com.example.demo.model.Recorrido
import com.example.demo.viewModel.RecorrViewModel
import java.text.SimpleDateFormat
import java.util.Date

class RecorrAddFragment : Fragment() {

    private var _binding: FragmentRecorrAddBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrAddFragmentArgs by navArgs()

    private lateinit var model: RecorrViewModel

    private lateinit var locationManager: LocationManager

    private var indicatorLight: ImageView? = null
    private val latLonIni = LatLong()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecorrAddBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[RecorrViewModel::class.java]

        val estadosMarea = resources.getStringArray(R.array.estado_marea)
        val mareasArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, estadosMarea)
        binding.spinnerMarea.adapter = mareasArrayAdapter

        binding.getPosicionIni.setOnClickListener { getPosicionActual() }
        binding.confirmarRecorridoButton.setOnClickListener { confirmarRecorrido() }

        return view
    }

    private fun confirmarRecorrido() {

        val recorrido = dataDesdeIU()
        model.insert(recorrido)

        Toast.makeText(activity, "Recorrido agregado correctamente", Toast.LENGTH_LONG).show()

        val action = RecorrAddFragmentDirections.goToRecorrListAction(args.idDia)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = view.findViewById(R.id.gpsLightRecorr)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dataDesdeIU(): Recorrido {

        val observador = binding.editObservador.text.toString()
        val areaRecorrida = binding.areaRecorr.text.toString()
        val meteo = binding.editTextMeteo.text.toString()
        val marea = binding.spinnerMarea.selectedItem.toString()

        val formato = requireContext().resources.getString(R.string.formato_fecha)
        val fechaIni = SimpleDateFormat(formato).format(Date())

        return Recorrido(args.idDia, observador, fechaIni, "",
            latLonIni.lat, latLonIni.lon, 0.0, 0.0,
            areaRecorrida, meteo, marea)
    }

    private fun getPosicionActual() {

        locationManager = requireActivity().getSystemService(LocationManager::class.java)

        if (checkLocationPermission()) {
            indicatorLight?.setImageResource(R.drawable.indicator_off)

            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        indicatorLight?.setImageResource(R.drawable.indicator_on)
                        updateLocationViews(location.latitude, location.longitude)
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

    private fun updateLocationViews(latitud: Double, longitud: Double) {

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