package com.example.demo.export

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.databinding.FragmentExportBinding

class ExportFragment : Fragment() {

    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarEmail() }
        binding.bluetoothBtn.setOnClickListener { usarBluetooth() }

        val check = binding.soyConcent
        val bluetoothTxt = binding.bluetoothText

        if(check.isChecked)
            binding.layMasterBt.visibility = View.GONE
        else
            binding.layMasterBt.visibility = View.VISIBLE

        check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                bluetoothTxt.text = "Recibir datos desde otros equipos"
                binding.layMasterBt.visibility = View.INVISIBLE
                binding.bluetoothBtn.text = "escuchar por BT"
            } else {
                bluetoothTxt.text = "Enviar mis datos a un concentrador"
                binding.layMasterBt.visibility = View.VISIBLE
                binding.bluetoothBtn.text = "enviar por BT"
            }
        }

        return binding.root
    }

    private fun usarBluetooth() {
        if (binding.soyConcent.isChecked)
            recibirDatos()
        else
            enviarConcentrador()
    }

    private fun recibirDatos() {
        val comunicacion = BluetoothManager(requireActivity(), requireContext())
        comunicacion.activarComoMTU()
    }

    private fun enviarConcentrador() {
        val comunicacion = BluetoothManager(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())
        comunicacion.activarComoRTU()
    }

    private fun enviarEmail() {
        TODO("Not yet implemented")
    }
}