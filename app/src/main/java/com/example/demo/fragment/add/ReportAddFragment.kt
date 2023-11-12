package com.example.demo.fragment.add

import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentReportAddBinding
import com.example.demo.model.Report
import com.example.demo.viewModel.ReportViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.min

const val REQUEST_TAKE_PHOTO = 1

class ReportAddFragment : Fragment() {

    private var _binding: FragmentReportAddBinding? = null
    private val binding get() = _binding!!
    lateinit var currentPhotoPath: String

    private val model: ReportViewModel by navGraphViewModels(R.id.app_navigation)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportAddBinding.inflate(inflater, container, false)
        val view = binding.root

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            rotateImage(reduceBitmap())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun continueToMap() {
        if (checkData()) {
            val title = _binding?.titleTextInput?.text.toString()
            val fishingType = _binding?.spinnerPtoObsCenso?.selectedItem.toString()
            val specie = _binding?.spinnerCtxSocial?.selectedItem.toString()
            val sdf = SimpleDateFormat("d/M/yyyy")
            val currentDate = sdf.format(Date())
            val report = Report(0, title, fishingType, specie, currentDate, currentPhotoPath, null, null)

            val action = ReportAddFragmentDirections.goToMapsFragmentAction(report)
            findNavController().navigate(action)
        }
    }

    private fun checkData(): Boolean {
        return if (_binding!!.titleTextInput.text?.isEmpty() == true) {
            Toast.makeText(activity, "Ingrese un título para la imagen", Toast.LENGTH_LONG).show()
            false
        } else if (_binding!!.captureImageView.drawable == null) {
            Toast.makeText(activity, "Capture una imagen", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun reduceBitmap(): Bitmap {
        val targetImageViewWidth = _binding!!.captureImageView.width
        val targetImageViewHeight = _binding!!.captureImageView.height

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

    private fun rotateImage(bitmap: Bitmap) {

        val exif = ExifInterface(currentPhotoPath)
        val orientation: Int =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        Log.i("orientation", orientation.toString())

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.setRotate(90F)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.setRotate(180F)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.setRotate(270F)
            }
        }

        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val outputStream: FileOutputStream = FileOutputStream(currentPhotoPath)
        rotatedBitmap?.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            outputStream
        )
        _binding!!.captureImageView.setImageBitmap(rotatedBitmap)
    }

}