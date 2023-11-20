package com.example.demo.fragment.add

import android.Manifest
import com.example.demo.adapter.PhotoAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.example.demo.databinding.FragmentReportAddBinding
import com.example.demo.model.Report
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.min

const val REQUEST_TAKE_PHOTO = 1
const val PERMISSION_REQUEST_CODE = 2

class ReportAddFragment : Fragment() {

    private var _binding: FragmentReportAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private val photoPaths = mutableListOf<String>()
    private val adapter = PhotoAdapter(photoPaths)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportAddBinding.inflate(inflater, container, false)
        val view = binding.root

        val photoRecyclerView: RecyclerView = _binding?.photoRecyclerView!!
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        photoRecyclerView.layoutManager = layoutManager
        photoRecyclerView.adapter = adapter

        val ptObsCenso = resources.getStringArray(R.array.op_punto_obs_censo)
        val ptObsCensoArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsCenso)

        _binding!!.spinnerPtoObsCenso.adapter = ptObsCensoArrayAdapter
        _binding!!.helpPtoObsCenso.setOnClickListener { ptoObsCensoInfo() }

        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val ctxSocialArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)

        _binding!!.spinnerCtxSocial.adapter = ctxSocialArrayAdapter
        _binding!!.helpCtxSocial.setOnClickListener { ctxSocialInfo() }

        _binding!!.photoButton.setOnClickListener { takePhoto() }
        _binding!!.continueButton.setOnClickListener { continueToMap() }

        return view
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun takePhoto() {
        Log.d("depurando codigo", "hasta aca llego")

        // Verificar permisos
        if (checkPermissions()) {
            // Los permisos están concedidos, continuar con la captura de fotos
            launchCamera()
        } else {
            // Los permisos no están concedidos, solicitarlos al usuario
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean = // Verificar permisos
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        // Solicitar permisos al usuario
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
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

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Verificar si los permisos fueron concedidos por el usuario
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Los permisos fueron concedidos, continuar con la captura de fotos
                launchCamera()
            } else {
                // Los permisos no fueron concedidos, mostrar un mensaje al usuario
                Toast.makeText(
                    requireContext(),
                    "Los permisos son necesarios para capturar fotos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"

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

        val ptoObsCenso = _binding?.spinnerPtoObsCenso?.selectedItem.toString()
        val ctxSocial = _binding?.spinnerCtxSocial?.selectedItem.toString()
        val sdf = SimpleDateFormat("d/M/yyyy")
        val currentDate = sdf.format(Date())

        val report =
            Report(0, ptoObsCenso, ctxSocial, currentDate, currentPhotoPath, null, null)

        val action = ReportAddFragmentDirections.goToMapsFragmentAction(report)
        findNavController().navigate(action)
    }

    private fun reduceBitmap(): Bitmap {
        val targetImageViewWidth = _binding!!.captureImageView.width
        val targetImageViewHeight = _binding!!.captureImageView.height

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
        _binding!!.captureImageView.setImageBitmap(rotatedBitmap)
    }
}