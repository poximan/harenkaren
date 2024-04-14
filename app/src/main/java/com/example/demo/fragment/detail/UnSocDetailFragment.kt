package com.example.demo.fragment.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.databinding.FragmentUnsocDetailBinding
import java.io.File

class UnSocDetailFragment : Fragment() {

    private var _binding: FragmentUnsocDetailBinding? = null
    private val binding get() = _binding!!
    private val args: UnSocDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocDetailBinding.inflate(inflater, container, false)

        _binding!!.ptoObservacion.text = args.unSocActual.ptoObsUnSoc
        _binding!!.ctxSocial.text = args.unSocActual.ctxSocial
        _binding!!.fechaObserv.text = args.unSocActual.date

        val file = File(args.unSocActual.photoPath)
        if (file.exists()) {
            val imageBitmap: Bitmap? = BitmapFactory.decodeFile(args.unSocActual.photoPath)
            rotateImage(imageBitmap!!)
        }

        _binding!!.volverButton.setOnClickListener { goBack() }
        _binding!!.editarButton.setOnClickListener { editar() }

        return binding.root
    }

    private fun editar() {
        val action = UnSocDetailFragmentDirections.goToUnSocUpdateAction(args.unSocActual)
        findNavController().navigate(action)
    }

    private fun goBack() {
        findNavController().navigate(R.id.goToUnSocListFromUnSocDetailAction)
    }

    private fun rotateImage(bitmap: Bitmap) {

        val exif = ExifInterface(args.unSocActual.photoPath!!)
        val orientation: Int =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

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
        _binding!!.captureImageView.setImageBitmap(rotatedBitmap)
    }
}