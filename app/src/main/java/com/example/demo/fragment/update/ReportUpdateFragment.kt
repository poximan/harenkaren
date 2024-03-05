package com.example.demo.fragment.update

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentCensoUpdateBinding
import com.example.demo.fragment.add.CensoAdd1Fragment.DbConstants.REQUEST_TAKE_PHOTO
import com.example.demo.model.Censo
import com.example.demo.viewModel.CensoViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

import kotlin.math.min

class ReportUpdateFragment : Fragment() {

    private var _binding: FragmentCensoUpdateBinding? = null
    private val binding get() = _binding!!
    private val args: ReportUpdateFragmentArgs by navArgs()
    private lateinit var currentPhotoPath: String

    private lateinit var model: CensoViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCensoUpdateBinding.inflate(inflater, container, false)
        val view = binding.root
        model = ViewModelProvider(this)[CensoViewModel::class.java]

        val ptObsCenso = resources.getStringArray(R.array.op_punto_obs_censo)
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val fishTypeArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsCenso)
        val speciesArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)

        currentPhotoPath = args.currentReport.photoPath
        _binding!!.spinnerUpdPtoObs.adapter = fishTypeArrayAdapter
        _binding!!.spinnerUpdCtxSocial.adapter = speciesArrayAdapter
        _binding!!.photoButton.setOnClickListener { takePhoto() }
        _binding!!.continueButton.setOnClickListener { continueToMap() }
        val fishTypeArrayPosition = fishTypeArrayAdapter.getPosition(args.currentReport.ptoObsCenso)
        _binding!!.spinnerUpdPtoObs.setSelection(fishTypeArrayPosition)

        val fishSpecieArrayPosition = speciesArrayAdapter.getPosition(args.currentReport.ctxSocial)
        _binding!!.spinnerUpdCtxSocial.setSelection(fishSpecieArrayPosition)

        val file = File(args.currentReport.photoPath)
        if (file.exists()) {
            val imageBitmap: Bitmap? = BitmapFactory.decodeFile(args.currentReport.photoPath)
            rotateImage(imageBitmap!!)
        }
        return view
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

    private fun reduceBitmap(): Bitmap {
        val targetImageViewWidth = _binding!!.updateCaptureImageView.width
        val targetImageViewHeight = _binding!!.updateCaptureImageView.height

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
        _binding!!.updateCaptureImageView.setImageBitmap(rotatedBitmap)
    }

    private fun continueToMap() {
        val ptoObsCenso = binding.spinnerUpdPtoObs.selectedItem.toString()
        val ctxSocial = binding.spinnerUpdCtxSocial.selectedItem.toString()

        val date = args.currentReport.date

        // If empty, photo path did not change
        val photoPath: String = if (currentPhotoPath == "") {
            args.currentReport.photoPath
        } else {
            currentPhotoPath
        }

        val updatedCenso = Censo(
            args.currentReport.id, 0,
            ptoObsCenso, ctxSocial, "",
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0, 0,
            date, args.currentReport.latitude, args.currentReport.longitude, photoPath
        )

        val action = ReportUpdateFragmentDirections.goToMapsFragmentFromReportUpdateFragment(updatedCenso)
        findNavController().navigate(action)
    }

}