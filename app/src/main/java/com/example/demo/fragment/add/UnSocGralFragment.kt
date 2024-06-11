package com.example.demo.fragment.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.PhotoAdapter
import com.example.demo.databinding.FragmentUnsocGralBinding
import com.example.demo.model.LatLong
import com.example.demo.viewModel.UnSocShareViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.KFunction2

class UnSocGralFragment() : Fragment() {

    companion object {
        private lateinit var colectar: (Int, Map<String, Any>) -> Unit
    }

    private val map: MutableMap<String, Any> = mutableMapOf()

    object DbConstants {
        const val PERMISSION_REQUEST_TAKE_PHOTO = 2
        const val PERMISSION_REQUEST_CAMERA = 3
        const val PERMISSION_REQUEST_LOCATION = 4
    }

    private var _binding: FragmentUnsocGralBinding? = null
    val binding get() = _binding!!

    private val sharedViewModel: UnSocShareViewModel by activityViewModels()

    private val photoPaths = mutableListOf<String>()
    private val adapter = PhotoAdapter(photoPaths)
    private var currentPhotoPath: String = ""

    private lateinit var locationManager: LocationManager
    private var indicatorLight: ImageView? = null
    private val latLon = LatLong()

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private lateinit var imageChangerRunnable: Runnable

    fun newInstance(
        colectarFunc: KFunction2<Int, Map<String, Any>, Unit>
    ): UnSocGralFragment {
        colectar = colectarFunc
        return UnSocGralFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUnsocGralBinding.inflate(inflater, container, false)
        val view = binding.root

        val photoRecyclerView: RecyclerView = binding.photoRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        photoRecyclerView.layoutManager = layoutManager
        photoRecyclerView.adapter = adapter

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
        binding.photoButton.setOnClickListener { takePhoto() }

        binding.spinnerAddPtoObs.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddCtxSocial.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddTpoSustrato.onItemSelectedListener = onItemSelectedListener
        binding.unSocComentario.addTextChangedListener(textWatcher)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = view.findViewById(R.id.gpsLightUnSoc)
    }

    override fun onResume() {
        super.onResume()

        try {
            latLon.lat = arguments?.getDouble("lat")!!
            latLon.lon = arguments?.getDouble("lon")!!
        } catch (e: NullPointerException) {
        }
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
        map["photo_path"] = currentPhotoPath
        map["comentario"] = binding.unSocComentario.text.toString()

        colectar(0, map)
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent?.id) {
                R.id.spinnerAddCtxSocial -> {
                    val selectedValue =
                        binding.spinnerAddCtxSocial.getItemAtPosition(position).toString()
                    sharedViewModel.setLastSelectedValue(selectedValue)
                }
            }
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

    private fun takePhoto() {
        // Verificar permisos
        if (checkCameraPermission()) {
            // Los permisos están concedidos, continuar con la captura de fotos
            launchCamera()
        } else {
            // Los permisos no están concedidos, solicitarlos al usuario
            requestCameraPermission()
        }
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

    private fun ptoObsUnidadSocialInfo() {
        findNavController().navigate(R.id.ptoObsUnSoc_activity)
    }

    private fun ctxSocialInfo() {
        findNavController().navigate(R.id.ctxSocial_activity)
    }

    private fun tpoSustratoInfo() {
        findNavController().navigate(R.id.tpoSustrato_activity)
    }

    /*
    RELACIONADAS CON PERMISOS DE CAMARA, ALMACENAMIENTO EXTERNO Y GPS
     */

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            DbConstants.PERMISSION_REQUEST_CAMERA
        )
    }

    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(requireContext(), ": error", Toast.LENGTH_LONG).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.demo.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        DbConstants.PERMISSION_REQUEST_TAKE_PHOTO
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val context = requireContext()
        when (requestCode) {
            DbConstants.PERMISSION_REQUEST_CAMERA -> {
                // Maneja los resultados de los permisos de la cámara
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Los permisos de la cámara fueron concedidos, puedes lanzar la cámara
                    launchCamera()
                } else {
                    // Los permisos de la cámara no fueron concedidos, muestra un mensaje al usuario
                    Toast.makeText(
                        context,
                        context.getString(R.string.socg_permCamara),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            DbConstants.PERMISSION_REQUEST_LOCATION -> {
                // Maneja los resultados de los permisos de ubicación
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Los permisos de ubicación fueron concedidos, pero no solicitamos actualizaciones aquí
                } else {
                    // Los permisos de ubicación no fueron concedidos, muestra un mensaje al usuario
                    Toast.makeText(
                        context,
                        context.getString(R.string.socg_permGPS),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp" + "_"

        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        currentPhotoPath = image.absolutePath

        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DbConstants.PERMISSION_REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val rotatedBitmap = rotateImage(reduceBitmap())
            saveRotatedBitmap(rotatedBitmap)
            photoPaths.add(currentPhotoPath)
            adapter.notifyDataSetChanged()
        }
    }

    private fun reduceBitmap(): Bitmap {
        val targetImageViewWidth = binding.captureImageView.width
        val targetImageViewHeight = binding.captureImageView.height

        if (targetImageViewWidth <= 0 || targetImageViewHeight <= 0) {
            throw IllegalArgumentException(requireContext().getString(R.string.socg_reduceBit))
        }

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        val cameraWidth = bmOptions.outWidth
        val cameraHeight = bmOptions.outHeight
        val scaleFactor =
            Integer.min(cameraWidth / targetImageViewWidth, cameraHeight / targetImageViewHeight)
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
    }

    private fun rotateImage(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(currentPhotoPath)
        val orientation: Int =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270F)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveRotatedBitmap(rotatedBitmap: Bitmap) {
        val outputStream = FileOutputStream(currentPhotoPath)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        binding.captureImageView.setImageBitmap(rotatedBitmap)
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
                DbConstants.PERMISSION_REQUEST_LOCATION
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
                        binding.latitud.text = requireContext().getString(R.string.varias_geopos)
                        binding.latitud.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_700
                            )
                        )
                    } else {
                        indicatorLight!!.setImageResource(R.drawable.indicator_off)
                        binding.latitud.text = requireContext().getString(R.string.varias_geopos)
                        binding.latitud.setTextColor(
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
