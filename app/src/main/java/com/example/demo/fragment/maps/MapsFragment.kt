package com.example.demo.fragment.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentMapsBinding
import com.example.demo.viewModel.CensoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MapsFragment : Fragment() {

    object DbConstants {
        const val PERMISSION_MAPS_CODE = 1
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val args: MapsFragmentArgs by navArgs()
    private lateinit var model: CensoViewModel
    private var marker: Marker? = null
    private var smallMarker: Bitmap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        model = ViewModelProvider(this)[CensoViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        _binding!!.goBackActionButton.setOnClickListener { goBack() }
        if (findNavController().previousBackStackEntry?.destination?.displayName!! == "com.example.demo:id/report_update_fragment") {
            _binding!!.sendReportActionButton.setOnClickListener { updateReport() }
        } else {
            _binding!!.sendReportActionButton.setOnClickListener { sendReport() }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // no hay permisos de ubicación fina ni aproximada, solicitar
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                DbConstants.PERMISSION_MAPS_CODE
            )
            return
        } else {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        val height = 60
        val width = 60
        val bitmapdraw =
            ContextCompat.getDrawable(requireContext(), R.drawable.map_icon) as BitmapDrawable
        val b = bitmapdraw.bitmap
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12F))
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Error al obtener la ubicación: ${e.message}")
            }

        Log.i("argumento", args.currentReport.toString())

        if (args.currentReport.latitude != null && args.currentReport.longitude != null) {
            val pos = LatLng(args.currentReport.latitude!!, args.currentReport.longitude!!)
            val snippet = String.format(
                Locale.getDefault(),
                "Tipo: %1$.15s - Especie: %2$.15s - Fecha: %3$.15s",
                args.currentReport.ptoObsCenso,
                args.currentReport.ctxSocial,
                args.currentReport.date
            )
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(pos)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!))
            )
            marker!!.showInfoWindow()
            if (findNavController().previousBackStackEntry?.destination?.displayName!! == "com.example.demo:id/report_update_fragment") {
                _binding!!.sendReportActionButton.show()
                setMapLongClick(googleMap)
            }
        } else {
            _binding!!.sendReportActionButton.show()
            setMapLongClick(googleMap)
        }
    }

    private fun setMapLongClick(map: GoogleMap) {

        map.setOnMapLongClickListener { latLng ->
            // Snippet --> texto adicional que se muestra debajo del título.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            if (marker == null) {
                marker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!))
                )
                args.currentReport.latitude = latLng.latitude
                args.currentReport.longitude = latLng.longitude
                marker!!.showInfoWindow()
            } else {
                marker!!.hideInfoWindow()
                marker!!.position = latLng
                marker!!.snippet = snippet
                args.currentReport.latitude = latLng.latitude
                args.currentReport.longitude = latLng.longitude
                marker!!.showInfoWindow()
            }
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun sendReport() {
        if (checkCoords()) {
            db.collection("reports").document().set(
                hashMapOf(
                    "ptoObs" to args.currentReport.ptoObsCenso,
                    "ctxSoc" to args.currentReport.ctxSocial,
                    "date" to args.currentReport.date,
                    "photo_path" to args.currentReport.photoPath,
                    "latitude" to args.currentReport.latitude,
                    "longitude" to args.currentReport.longitude
                )
            )
            model.insert(args.currentReport)
            Toast.makeText(activity, "Reporte agregado correctamente", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.my_censos_fragment)
        }
    }

    private fun checkCoords(): Boolean {
        if (args.currentReport.latitude == null && args.currentReport.longitude == null) {
            Toast.makeText(activity, "Seleccione una ubicación en el mapa", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun updateReport() {

        /* TODO Los reportes NO se actualizan en la nube. Los id's no coinciden, ya que en la bd local se guardan
         con un id numerico autoincrementaado, y en firebase se genera un id random.
         Tampoco se actualizan en la bd local si se hace desde la nube.
         Esto habría que verlo en el futuro si el proyecto prospera
        */
        db.collection("reports").document(args.currentReport.id.toString()).update(
            "pto_obs_censo", args.currentReport.ptoObsCenso,
            "ctx_social", args.currentReport.ctxSocial,
            "date", args.currentReport.date,
            "photo_path", args.currentReport.photoPath,
            "latitude", args.currentReport.latitude,
            "longitude", args.currentReport.longitude
        )

        model.update(args.currentReport)
        Toast.makeText(activity, "Reporte editado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.my_censos_fragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            DbConstants.PERMISSION_MAPS_CODE -> {
                // Verificar si la solicitud de permiso fue cancelada.
                if (grantResults.isEmpty()) {
                    // La solicitud de permiso fue cancelada.
                    Log.i("GPS", "Permiso cancelado")
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, puedes realizar acciones que requieran el permiso aquí.
                    Log.d("GPS", "Permiso concedido")
                    callback
                } else {
                    // Permiso denegado, puedes mostrar un mensaje al usuario o tomar otra acción.
                    Log.d("GPS", "Permiso denegado")
                }
            }
        }
    }
}

/*
class MapsFragment : Fragment() {

    object DbConstants {
        const val PERMISSION_REQUEST_CODE = 1
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val args: MapsFragmentArgs by navArgs()
    private lateinit var model: ReportViewModel
    private var marker: Marker? = null
    private var smallMarker: Bitmap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        val height = 120
        val width = 120
        val bitmapdraw =
            ContextCompat.getDrawable(requireContext(), R.drawable.map_icon) as BitmapDrawable
        val b = bitmapdraw.bitmap
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        if (hasLocationPermission()) {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12F))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Error al obtener la ubicación: ${e.message}")
                }

            if (args.currentReport.latitude != null && args.currentReport.longitude != null) {
                val pos = LatLng(args.currentReport.latitude!!, args.currentReport.longitude!!)
                val snippet = String.format(
                    Locale.getDefault(),
                    "Tipo: %1$.15s - Especie: %2$.15s - Fecha: %3$.15s",
                    args.currentReport.ptoObsCenso,
                    args.currentReport.ctxSocial,
                    args.currentReport.date
                )
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!))
                )
                marker!!.showInfoWindow()
                if (findNavController().previousBackStackEntry?.destination?.displayName!! == "com.example.demo:id/report_update_fragment") {
                    _binding!!.sendReportActionButton.show()
                    setMapLongClick(googleMap)
                }
            } else {
                _binding!!.sendReportActionButton.show()
                setMapLongClick(googleMap)
            }
        } else {
            // Si no tienes permisos, solicítalos
            requestPermissions()
        }
    }

    // Función para verificar si tienes permisos de ubicación
    private fun hasLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Si la versión de Android es anterior a Marshmallow, los permisos se otorgan en el manifiesto
            true
        }
    }

    // Función para solicitar permisos de ubicación
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                DbConstants.PERMISSION_REQUEST_CODE
            )
        }
    }

    // Sobrescribe este método para manejar el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DbConstants.PERMISSION_REQUEST_CODE) {
            // Verificar si el usuario concedió los permisos
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO El usuario concedió permisos, pero hay que levantar a mano el servicio
            } else {
                // TODO El usuario no concedió los permisos, mostrar un mensaje
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        model = ViewModelProvider(this)[ReportViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        _binding!!.goBackActionButton.setOnClickListener { goBack() }
        if (findNavController().previousBackStackEntry?.destination?.displayName!! == "com.example.demo:id/report_update_fragment") {
            _binding!!.sendReportActionButton.setOnClickListener { updateReport() }
        } else {
            _binding!!.sendReportActionButton.setOnClickListener { sendReport() }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setMapLongClick(map: GoogleMap) {

        map.setOnMapLongClickListener { latLng ->
            // Snippet --> texto adicional que se muestra debajo del título.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            if (marker == null) {
                marker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker!!))
                )
                args.currentReport.latitude = latLng.latitude
                args.currentReport.longitude = latLng.longitude
                marker!!.showInfoWindow()
            } else {
                marker!!.hideInfoWindow()
                marker!!.position = latLng
                marker!!.snippet = snippet
                args.currentReport.latitude = latLng.latitude
                args.currentReport.longitude = latLng.longitude
                marker!!.showInfoWindow()
            }
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun sendReport() {
        if (checkCoords()) {
            db.collection("reports").document().set(
                hashMapOf(
                    "ptoObs" to args.currentReport.ptoObsCenso,
                    "ctxSoc" to args.currentReport.ctxSocial,
                    "date" to args.currentReport.date,
                    "photo_path" to args.currentReport.photoPath,
                    "latitude" to args.currentReport.latitude,
                    "longitude" to args.currentReport.longitude
                )
            )
            model.insert(args.currentReport)
            Toast.makeText(activity, "Reporte agregado correctamente", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.my_reports_fragment)
        }
    }

    private fun checkCoords(): Boolean {
        if (args.currentReport.latitude == null && args.currentReport.longitude == null) {
            Toast.makeText(activity, "Seleccione una ubicación en el mapa", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun updateReport() {

        /* TODO Los reportes NO se actualizan en la nube. Los id's no coinciden, ya que en la bd local se guardan
         con un id numerico autoincrementaado, y en firebase se genera un id random.
         Tampoco se actualizan en la bd local si se hace desde la nube.
         Esto habría que verlo en el futuro si el proyecto prospera
        */
        db.collection("reports").document(args.currentReport.id.toString()).update(
            "pto_obs_censo", args.currentReport.ptoObsCenso,
            "ctx_social", args.currentReport.ctxSocial,
            "date", args.currentReport.date,
            "photo_path", args.currentReport.photoPath,
            "latitude", args.currentReport.latitude,
            "longitude", args.currentReport.longitude
        )

        model.updateReport(args.currentReport)
        Toast.makeText(activity, "Reporte editado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.my_reports_fragment)
    }
}
*/