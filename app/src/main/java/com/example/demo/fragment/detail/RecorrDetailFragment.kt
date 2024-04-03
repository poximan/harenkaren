package com.example.demo.fragment.detail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentRecorrDetailBinding
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.model.LatLong

class RecorrDetailFragment : Fragment() {

    private var _binding: FragmentRecorrDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecorrDetailFragmentArgs by navArgs()

    private lateinit var locationManager: LocationManager

    private var indicatorLight: ImageView? = null
    private val latLonIni = LatLong()
    private val latLonFin = LatLong()

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorrDetailBinding.inflate(inflater, container, false)

        _binding!!.editObservador.text = args.recorrActual.observador.toEditable()
        _binding!!.areaRecorr.text = args.recorrActual.areaRecorrida.toEditable()

        _binding!!.volverButton.setOnClickListener { goBack() }
        _binding!!.verUnSocButton.setOnClickListener { verUnidadSocial() }

        binding.getPosicionIni.setOnClickListener { getPosicionActual(binding.latitudIni, binding.longitudIni, latLonIni) }
        binding.getPosicionFin.setOnClickListener { getPosicionActual(binding.latitudFin, binding.longitudFin, latLonFin) }

        _binding!!.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // Habilitar o deshabilitar los componentes según el estado del checkbox
            _binding!!.editObservador.isEnabled = isChecked
            _binding!!.areaRecorr.isEnabled = isChecked

            _binding!!.getPosicionIni.isEnabled = isChecked
            _binding!!.getPosicionFin.isEnabled = isChecked
        }

        return binding.root
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

    private fun getPosicionActual(latDestino: TextView, lonDestino: TextView, latLongDestino: LatLong) {

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