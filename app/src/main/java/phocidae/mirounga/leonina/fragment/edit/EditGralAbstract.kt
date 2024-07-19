package phocidae.mirounga.leonina.fragment.edit

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.fragment.add.AddGralAbstract

abstract class EditGralAbstract : SuperEdit() {

    private lateinit var locationManager: LocationManager
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private lateinit var imageChangerRunnable: Runnable

    var indicatorLight: ImageView? = null

    fun obtenerPosicionSpinner(entradaBuscada: String?, spinnerActual: Spinner): Int {
        val opcionesAdapter = spinnerActual.adapter // Obtener el adaptador del Spinner
        val opciones = mutableListOf<String>()
        if (opcionesAdapter != null) {
            for (i in 0 until opcionesAdapter.count) {
                opciones.add(opcionesAdapter.getItem(i).toString())
            }
        }
        return opciones.indexOfFirst { it.equals(entradaBuscada, ignoreCase = true) }
    }

    fun getPosicionActual(textView: TextView) {

        locationManager = requireActivity().getSystemService(LocationManager::class.java)
        if (checkLocationPermission()) {

            startImageChanger(textView)
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
                        Toast.makeText(
                            context,
                            context.getString(R.string.varias_gpsHab),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                null
            )
        } else {
            requestLocationPermission()
        }
    }

    abstract fun updateLocationViews(latitud: Double, longitud: Double)

    private fun startImageChanger(textView: TextView) {
        isRunning = true
        imageChangerRunnable = object : Runnable {
            private var isImageChanged = false

            override fun run() {
                if (isRunning) {
                    if (isImageChanged) {
                        indicatorLight!!.setImageResource(R.drawable.indicator_on)
                        textView.text = requireContext().getString(R.string.varias_geopos)
                        textView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_700
                            )
                        )
                    } else {
                        indicatorLight!!.setImageResource(R.drawable.indicator_off)
                        textView.text = requireContext().getString(R.string.varias_geopos)
                        textView.setTextColor(
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

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AddGralAbstract.DbConstants.PERMISSION_REQUEST_LOCATION
            )
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
}
