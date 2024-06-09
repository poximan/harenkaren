package com.example.demo.fragment.detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocGralBinding
import com.example.demo.fragment.add.UnSocGralFragment
import com.example.demo.model.LatLong
import com.example.demo.model.UnidSocial
import kotlin.reflect.KFunction2

class UnSocGralDetailFragment(private val context: Context) : Fragment() {

    companion object {
        private lateinit var colectar: (Int, Map<String, Any>) -> Unit
    }

    private val map: MutableMap<String, Any> = mutableMapOf()

    private var _binding: FragmentUnsocGralBinding? = null
    val binding get() = _binding!!

    private lateinit var locationManager: LocationManager
    private var indicatorLight: ImageView? = null
    private val latLon = LatLong()

    private lateinit var unSocEditable: UnidSocial

    fun editInstance(
        colectarFunc: KFunction2<Int, Map<String, Any>, Unit>,
        unSoc: UnidSocial
    ): UnSocGralDetailFragment {
        colectar = colectarFunc
        unSocEditable = unSoc
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUnsocGralBinding.inflate(inflater, container, false)
        val view = binding.root

        // punto de observacion
        val ptObsUnSoc = resources.getStringArray(R.array.op_punto_obs_unsoc)
        val ptObsUnSocArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsUnSoc)

        binding.spinnerAddPtoObs.adapter = ptObsUnSocArrayAdapter
        binding.helpPtoObsUnSoc.setOnClickListener { ptoObsUnidadSocialInfo() }

        // contexto social
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val ctxSocialArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)

        binding.spinnerAddCtxSocial.adapter = ctxSocialArrayAdapter
        binding.helpCtxSocial.setOnClickListener { ctxSocialInfo() }

        // tipo de sustrato en playa
        val tpoSustrato = resources.getStringArray(R.array.op_tipo_sustrato)
        val tpoSustratoArrayAdapter =
            ArrayAdapter(view.context, R.layout.dropdown_item, tpoSustrato)

        binding.spinnerAddTpoSustrato.adapter = tpoSustratoArrayAdapter
        binding.helpTpoSustrato.setOnClickListener { tpoSustratoInfo() }

        binding.getPosicion.setOnClickListener { getPosicionActual() }
        binding.icono.setOnClickListener { goGraficar() }

        binding.spinnerAddPtoObs.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddCtxSocial.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddTpoSustrato.onItemSelectedListener = onItemSelectedListener

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = view.findViewById(R.id.gpsLightUnSoc)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            outState.putParcelable("unSocEditable", unSocEditable)
        } catch (e: UninitializedPropertyAccessException) {
            Log.i(
                "estadoRotacion",
                "falso positivo para UninitializedPropertyAccessException en ${toString()}" +
                        " por rotacion de pantalla + criterio de anticipacion TabLayout/ViewPager que pretende salvar datos" +
                        " antes que entre en RUN el fragmento contenedor"
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            try {
                unSocEditable = it.getParcelable("unSocEditable")!!
            } catch (e: NullPointerException) {
                Log.i(
                    "estadoRotacion", "falso positivo para NullPointerException en ${toString()}." +
                            " por rotacion de pantalla + criterio de anticipacion TabLayout/ViewPager que pretende recuperar datos" +
                            " antes que entre en RUN el fragmento contenedor"
                )
            }
        }
    }

    private fun goGraficar() {
        val action =
            UnSocDetailFragmentDirections.goToGrafDesdeUnSocAction(unSocEditable)
        findNavController().navigate(action)
    }

    private fun cargarDatos() {
        var indice = obtenerPosicionSpinner(unSocEditable.ptoObsUnSoc, binding.spinnerAddPtoObs)
        binding.spinnerAddPtoObs.setSelection(indice)

        indice = obtenerPosicionSpinner(unSocEditable.ctxSocial, binding.spinnerAddCtxSocial)
        binding.spinnerAddCtxSocial.setSelection(indice)

        indice = obtenerPosicionSpinner(unSocEditable.tpoSustrato, binding.spinnerAddTpoSustrato)
        binding.spinnerAddTpoSustrato.setSelection(indice)

        binding.unSocComentario.setText(unSocEditable.comentario)

        binding.latitud.text = unSocEditable.latitud.toString()
        binding.longitud.text = unSocEditable.longitud.toString()

        binding.linearLayout6.visibility = View.INVISIBLE
        binding.photoButton.visibility = View.INVISIBLE

        binding.unSocComentario.addTextChangedListener(textWatcher)
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

    override fun onResume() {
        super.onResume()

        try {
            latLon.lat = arguments?.getDouble("lat")!!
            latLon.lon = arguments?.getDouble("lon")!!
        } catch (e: NullPointerException) {
        }
        cargarDatos()
        cargarMap()
        mostrarEnPantalla()
    }

    override fun onPause() {
        super.onPause()

        val bundle = Bundle().apply {
            putDouble("lat", latLon.lat)
            putDouble("lon", latLon.lon)
        }
        arguments = bundle
        cargarMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun cargarMap() {

        map["pto_observacion"] = binding.spinnerAddPtoObs.selectedItem.toString()
        map["ctx_social"] = binding.spinnerAddCtxSocial.selectedItem.toString()
        map["tpo_sustrato"] = binding.spinnerAddTpoSustrato.selectedItem.toString()
        map["latitud"] = latLon.lat
        map["longitud"] = latLon.lon
        map["photo_path"] = unSocEditable.photoPath as String
        map["comentario"] = binding.unSocComentario.text.toString()

        colectar(0, map)
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            cargarMap()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            cargarMap()
        }
    }

    private fun updateLocationViews(latitud: Double, longitud: Double) {
        latLon.lat = latitud
        latLon.lon = longitud

        mostrarEnPantalla()
        cargarMap()
    }

    private fun mostrarEnPantalla() {
        val lat = String.format("%.6f", latLon.lat)
        val lon = String.format("%.6f", latLon.lon)

        binding.latitud.text = lat
        binding.longitud.text = lon
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

    private fun ptoObsUnidadSocialInfo() {
        findNavController().navigate(R.id.ptoObsUnSoc_activity)
    }

    private fun ctxSocialInfo() {
        findNavController().navigate(R.id.ctxSocial_activity)
    }

    private fun tpoSustratoInfo() {
        findNavController().navigate(R.id.tpoSustrato_activity)
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

    override fun toString(): String {
        return context.getString(R.string.socg_toString)
    }
}
