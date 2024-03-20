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
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.PhotoAdapter
import com.example.demo.databinding.FragmentUnsocAdd1Binding
import com.example.demo.model.UnidSocial
import com.example.demo.model.LatLong
import com.example.demo.viewModel.UnSocViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.Date

class UnSocAdd1Fragment : Fragment() {

    object DbConstants {
        const val REQUEST_TAKE_PHOTO = 2
        const val PERMISSION_REQUEST_CAMERA = 3
        const val PERMISSION_REQUEST_LOCATION = 4
    }
    private val args: UnSocAdd1FragmentArgs by navArgs()

    private var _binding: FragmentUnsocAdd1Binding? = null
    private val binding get() = _binding!!

    private lateinit var model: UnSocViewModel

    private var currentPhotoPath: String = ""
    private val photoPaths = mutableListOf<String>()
    private val adapter = PhotoAdapter(photoPaths)

    private lateinit var locationManager: LocationManager
    private var indicatorLight: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUnsocAdd1Binding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[UnSocViewModel::class.java]

        val photoRecyclerView: RecyclerView = binding.photoRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        photoRecyclerView.layoutManager = layoutManager
        photoRecyclerView.adapter = adapter

        binding.getPosicion.setOnClickListener { getPosicionActual() }
        binding.photoButton.setOnClickListener { takePhoto() }
        binding.mapButton.setOnClickListener { continueToMap() }
        binding.contarAnimales.setOnClickListener { contarAnimales() }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = view.findViewById(R.id.gpsLightUnSoc)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun contarAnimales() {

        val lat = binding.latitud.text.toString().toDouble()
        val lon = binding.longitud.text.toString().toDouble()
        val comentario = binding.unSocComentario.text.toString()

        try {

            if (currentPhotoPath.isEmpty() && false)    // TODO ver condicion de entrada
                throw UninitializedPropertyAccessException()

            val action = UnSocAdd1FragmentDirections.goToAdd2FragmentAction(args.idRecorrido, LatLong(lat , lon), currentPhotoPath, comentario)
            findNavController().navigate(action)

        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(
                requireContext(),
                "Debe tomar una foto primero",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(activity, ": error", Toast.LENGTH_LONG).show()
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
                    startActivityForResult(takePictureIntent, DbConstants.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            DbConstants.PERMISSION_REQUEST_CAMERA -> {
                // Maneja los resultados de los permisos de la cámara
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Los permisos de la cámara fueron concedidos, puedes lanzar la cámara
                    launchCamera()
                } else {
                    // Los permisos de la cámara no fueron concedidos, muestra un mensaje al usuario
                    Toast.makeText(
                        requireContext(),
                        "Los permisos de la cámara son necesarios para tomar fotos.",
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
                        requireContext(),
                        "Los permisos de ubicación son necesarios para obtener la posición actual.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
        if (requestCode == DbConstants.REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val rotatedBitmap = rotateImage(reduceBitmap())
            saveRotatedBitmap(rotatedBitmap)
            photoPaths.add(currentPhotoPath)
            adapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun continueToMap() {

        try {

            if (currentPhotoPath.isEmpty())
                throw UninitializedPropertyAccessException()

            val lat = binding.latitud.text.toString().toDouble()
            val lon = binding.longitud.text.toString().toDouble()

            val action = UnSocAdd1FragmentDirections.goToMapsFragmentAction(UnidSocial(1,lat,lon,currentPhotoPath))
            findNavController().navigate(action)

        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(
                requireContext(),
                "Debe tomar una foto primero",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun reduceBitmap(): Bitmap {
        val targetImageViewWidth = binding.captureImageView.width
        val targetImageViewHeight = binding.captureImageView.height

        if (targetImageViewWidth <= 0 || targetImageViewHeight <= 0) {
            throw IllegalArgumentException("Las dimensiones del ImageView no pueden ser cero o negativas.")
        }

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        val cameraWidth = bmOptions.outWidth
        val cameraHeight = bmOptions.outHeight
        val scaleFactor =
            min(cameraWidth / targetImageViewWidth, cameraHeight / targetImageViewHeight)
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

    @RequiresApi(Build.VERSION_CODES.R)
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
                DbConstants.PERMISSION_REQUEST_LOCATION
            )
        }
    }
}

