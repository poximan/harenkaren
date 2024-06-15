package com.example.demo.fragment.detail

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
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

    private val latLonIni = LatLong()
    private val latLonFin = LatLong()

    private lateinit var locationManager: LocationManager
    private var indicatorLight: ImageView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private lateinit var imageChangerRunnable: Runnable

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

        val indice = obtenerPosicionSpinner(args.recorrActual.marea, binding.spinnerMarea)
        binding.spinnerMarea.setSelection(indice)
        binding.spinnerMarea.isEnabled = false

        binding.horaIni.text = "${requireContext().getString(R.string.rec_horaini)}: " + args.recorrActual.fechaIni
        binding.horaFin.text = "${requireContext().getString(R.string.rec_horafin)}: " + args.recorrActual.fechaFin

        indicatorLight = binding.gpsLight
        latLonIni.lat = args.recorrActual.latitudIni
        latLonIni.lon = args.recorrActual.longitudIni
        latLonFin.lat = args.recorrActual.latitudFin
        latLonFin.lon = args.recorrActual.longitudFin

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getPosicionFin.setOnClickListener { getPosicionActual() }
        binding.volverButton.setOnClickListener { goBack() }
        binding.verUnSocButton.setOnClickListener { verUnidadSocial() }
        binding.confirmarButton.setOnClickListener { guardarCambios() }

        // Habilitar o deshabilitar los componentes según el estado del checkbox
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.editObservador.isEnabled = isChecked
            binding.areaRecorr.isEnabled = isChecked
            binding.meteo.isEnabled = isChecked
            binding.spinnerMarea.isEnabled = isChecked
            binding.getPosicionFin.isEnabled = isChecked
            binding.confirmarButton.isEnabled = isChecked
        }
        mostrarEnPantalla()
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

        val context = requireContext()
        Toast.makeText(context, context.getString(R.string.rec_confirmarEdit), Toast.LENGTH_LONG).show()

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
        val action =
            RecorrDetailFragmentDirections.goToUnSocListFromRecorrDetailAction(args.recorrActual.id)
        findNavController().navigate(action)
    }

    private fun goBack() {
        val action = RecorrDetailFragmentDirections.goToRecorrListAction(args.recorrActual.diaId)
        findNavController().navigate(action)
    }

    private fun getPosicionActual() {

        locationManager = requireActivity().getSystemService(LocationManager::class.java)
        if (checkLocationPermission()) {

            startImageChanger()
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        stopImageChanger()
                        updateLocationViews(location.latitude, location.longitude)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        val context = requireContext()
                        Toast.makeText(context, context.getString(R.string.varias_gpsHab), Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } else {
            requestLocationPermission()
        }
    }

    private fun updateLocationViews(latitud: Double, longitud: Double) {

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

    private fun startImageChanger() {
        isRunning = true
        imageChangerRunnable = object : Runnable {
            private var isImageChanged = false

            override fun run() {
                if (isRunning) {
                    if (isImageChanged) {
                        indicatorLight!!.setImageResource(R.drawable.indicator_on)
                        binding.latitudFin.text = requireContext().getString(R.string.varias_geopos)
                        binding.latitudFin.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_700
                            )
                        )
                    } else {
                        indicatorLight!!.setImageResource(R.drawable.indicator_off)
                        binding.latitudFin.text = requireContext().getString(R.string.varias_geopos)
                        binding.latitudFin.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                    }
                    isImageChanged = !isImageChanged
                    handler.postDelayed(this, 800)
                }
            }
        }

        Thread {
            handler.post(imageChangerRunnable)
        }.start()
    }

    private fun stopImageChanger() {
        isRunning = false
        handler.removeCallbacks(imageChangerRunnable)
        indicatorLight!!.setImageResource(R.drawable.indicator_on)
    }
}