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
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocUpdateBinding
import com.example.demo.fragment.add.UnSocAdd1Fragment.DbConstants.REQUEST_TAKE_PHOTO
import com.example.demo.viewModel.UnSocViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

import kotlin.math.min

class UnSocUpdateFragment : Fragment() {

    private var _binding: FragmentUnsocUpdateBinding? = null
    private val binding get() = _binding!!
    private val args: UnSocUpdateFragmentArgs by navArgs()
    private lateinit var currentPhotoPath: String

    private lateinit var model: UnSocViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocUpdateBinding.inflate(inflater, container, false)
        val view = binding.root
        model = ViewModelProvider(this)[UnSocViewModel::class.java]

        val ptObservacion = resources.getStringArray(R.array.op_punto_obs_unsoc)
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val fishTypeArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObservacion)
        val speciesArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)

        currentPhotoPath = args.unSocActual.photoPath!!

        _binding!!.spinnerUpdPtoObs.adapter = fishTypeArrayAdapter
        _binding!!.spinnerUpdCtxSocial.adapter = speciesArrayAdapter
        _binding!!.photoButton.setOnClickListener { takePhoto() }

        val fishTypeArrayPosition = fishTypeArrayAdapter.getPosition(args.unSocActual.ptoObsUnSoc)
        _binding!!.spinnerUpdPtoObs.setSelection(fishTypeArrayPosition)

        val fishSpecieArrayPosition = speciesArrayAdapter.getPosition(args.unSocActual.ctxSocial)
        _binding!!.spinnerUpdCtxSocial.setSelection(fishSpecieArrayPosition)

        val file = File(args.unSocActual.photoPath)
        if (file.exists()) {
            val imageBitmap: Bitmap? = BitmapFactory.decodeFile(args.unSocActual.photoPath)
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
}