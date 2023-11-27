package com.example.demo.fragment.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.PhotoAdapter
import com.example.demo.databinding.FragmentReportAddBinding
import com.example.demo.model.Report
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.min

const val REQUEST_TAKE_PHOTO = 2
const val PERMISSION_REQUEST_CAMERA = 3
const val PERMISSION_REQUEST_LOCATION = 4

class ReportAddFragment : Fragment() {

    private var _binding: FragmentReportAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private val photoPaths = mutableListOf<String>()
    private val adapter = PhotoAdapter(photoPaths)

    private lateinit var locationManager: LocationManager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportAddBinding.inflate(inflater, container, false)
        val view = binding.root

        val photoRecyclerView: RecyclerView = binding.photoRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        photoRecyclerView.layoutManager = layoutManager
        photoRecyclerView.adapter = adapter

        // punto de observacion
        val ptObsCenso = resources.getStringArray(R.array.op_punto_obs_censo)
        val ptObsCensoArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsCenso)

        binding.spinnerPtoObsCenso.adapter = ptObsCensoArrayAdapter
        binding.helpPtoObsCenso.setOnClickListener { ptoObsCensoInfo() }

        // contexto social
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val ctxSocialArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)

        binding.spinnerCtxSocial.adapter = ctxSocialArrayAdapter
        binding.helpCtxSocial.setOnClickListener { ctxSocialInfo() }

        // tipo de sustrato en playa
        val tpoSustrato = resources.getStringArray(R.array.op_tipo_sustrato)
        val tpoSustratoArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, tpoSustrato)

        binding.spinnerTpoSustrato.adapter = tpoSustratoArrayAdapter
        binding.helpTpoSustrato.setOnClickListener { tpoSustratoInfo() }

        binding.photoButton.setOnClickListener { takePhoto() }
        binding.continueButton.setOnClickListener { continueToMap() }
        binding.getPosicion.setOnClickListener { getLocation() }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctxSocialSpinner = view.findViewById<Spinner>(R.id.spinner_ctxSocial)
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)

        val linearLayout4 = view.findViewById<LinearLayout>(R.id.linearLayout4)
        val linearLayout5 = view.findViewById<LinearLayout>(R.id.linearLayout5)

        ctxSocialSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (ctxSocialSpinner.selectedItem == ctxSocial[0] || ctxSocialSpinner.selectedItem == ctxSocial[1]) {
                    linearLayout4.visibility = View.VISIBLE
                    linearLayout5.visibility = View.VISIBLE
                } else {
                    linearLayout4.visibility = View.GONE
                    linearLayout5.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita hacer nada aquí.
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun ptoObsCensoInfo() {
        findNavController().navigate(R.id.ptoObsCensoAction)
    }

    private fun ctxSocialInfo() {
        findNavController().navigate(R.id.ctxSocialAction)
    }

    private fun tpoSustratoInfo() {
        findNavController().navigate(R.id.tpoSustratoAction)
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
            PERMISSION_REQUEST_CAMERA
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

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
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
            PERMISSION_REQUEST_CAMERA -> {
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
            PERMISSION_REQUEST_LOCATION -> {
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
        val imageFileName = "JPEG_$timeStamp"+"_"

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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val rotatedBitmap = rotateImage(reduceBitmap())
            saveRotatedBitmap(rotatedBitmap)
            photoPaths.add(currentPhotoPath)
            adapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun continueToMap() {
        val ptoObsCenso = binding.spinnerPtoObsCenso.selectedItem.toString()
        val ctxSocial = binding.spinnerCtxSocial.selectedItem.toString()
        val sdf = SimpleDateFormat("d/M/yyyy")
        val currentDate = sdf.format(Date())

        try {
            val report =
                Report(0, ptoObsCenso, ctxSocial, currentDate, currentPhotoPath, null, null)
            val action = ReportAddFragmentDirections.goToMapsFragmentAction(report)

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
        val outputStream: FileOutputStream = FileOutputStream(currentPhotoPath)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        binding.captureImageView.setImageBitmap(rotatedBitmap)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = requireActivity().getSystemService(LocationManager::class.java)

        if (checkLocationPermission()) {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocationViews(location.latitude, location.longitude)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                        // No se necesita implementar aquí
                    }

                    override fun onProviderEnabled(provider: String) {
                        // No se necesita implementar aquí
                    }

                    override fun onProviderDisabled(provider: String) {
                        val message =
                            "El GPS está deshabilitado. Por favor, habilítelo en Configuraciones."
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } else {
            requestLocationPermission()
        }
    }

    private fun updateLocationViews(latitude: Double, longitude: Double) {
        binding.latitud.text = latitude.toString()
        binding.longitud.text = longitude.toString()
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
                PERMISSION_REQUEST_LOCATION
            )
        }
    }
}