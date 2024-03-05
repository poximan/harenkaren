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
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentCircuitoAddBinding
import com.example.demo.model.Circuito
import com.example.demo.viewModel.CircuitoViewModel
import java.text.SimpleDateFormat
import java.util.Date

class CircuitoAddFragment : Fragment() {

    private var _binding: FragmentCircuitoAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: CircuitoViewModel

    private lateinit var locationManager: LocationManager
    private var indicatorLight: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCircuitoAddBinding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[CircuitoViewModel::class.java]

        binding.getPosicion.setOnClickListener { getPosicionActual() }
        binding.confirmarCircuitoButton.setOnClickListener { confirmarCircuito() }
        return view
    }

    private fun confirmarCircuito() {

        val circuito = dataDesdeIU()
        model.insertCircuito(circuito)

        Toast.makeText(activity, "Circuito agregado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.mis_circuitos_fragment)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dataDesdeIU(): Circuito {

        val observador = binding.editTextObservador.text.toString()
        val areaRecorrida = binding.editTextAreaRecorrida.text.toString()
        val meteo = binding.editTextMeteo.text.toString()

        val lat = binding.latitud.text.toString().toDouble()
        val lon = binding.longitud.text.toString().toDouble()

        val timeStamp = SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Date())

        return Circuito(0,observador, timeStamp,lat,lon,1.0,2.0,areaRecorrida,meteo)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getPosicionActual() {

        locationManager = requireActivity().getSystemService(LocationManager::class.java)

        if (checkLocationPermission()) {
            indicatorLight?.setImageResource(R.drawable.indicator_off)

            /*
            val proveedores : MutableList<String> = locationManager.allProviders
            val indiceGps = proveedores.indexOf("gps")
            val provGps : String = proveedores[indiceGps]

            locationManager.getCurrentLocation(provGps,null, Executors.newSingleThreadExecutor()) { location: Location? ->
                // Manejar la ubicación obtenida
                location?.let {
                    // Hacer algo con la ubicación (por ejemplo, mostrarla en un TextView)
                    indicatorLight?.setImageResource(R.drawable.indicator_on)
                    updateLocationViews(location.latitude, location.longitude)
                }
            }
            */
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

    private fun updateLocationViews(latitude: Double, longitude: Double) {
        binding.latitud.text = latitude.toString()
        binding.longitud.text = longitude.toString()
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                CensoAdd1Fragment.DbConstants.PERMISSION_REQUEST_LOCATION
            )
        }
    }
}

