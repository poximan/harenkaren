package com.example.demo.export

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.databinding.FragmentExportBinding

class ExportFragment : Fragment(), GestorBT.MessageReceivedCallback {

    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
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
                if(binding.radioBt.isChecked)
                    binding.bluetoothBtn.text = "escuchar por BT"
                if(binding.radioWifi.isChecked)
                    binding.bluetoothBtn.text = "escuchar por WF"

                binding.recepcionBt.text = "esperando datos desde remotas"
            } else {
                bluetoothTxt.text = "Enviar mis datos a un concentrador"

                binding.layMasterBt.visibility = View.VISIBLE
                if(binding.radioBt.isChecked){
                    binding.bluetoothBtn.text = "enviar por BT"
                    binding.idMasterBt.text = "ID del concentrador"
                }
                if(binding.radioWifi.isChecked) {
                    binding.bluetoothBtn.text = "enviar por WF"
                    binding.idMasterBt.text = "IP del concentrador"
                }

                val comunicacion = GestorBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext(), this)

                val datosConcatenados = StringBuilder()
                for (elemento in comunicacion.obtenerDispositivosEmparejados()) {
                    datosConcatenados.append("MAC: ${elemento.address}, Alias: ${elemento.name}\n")
                }

                binding.recepcionBt.text = datosConcatenados.toString()
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
        if(binding.radioBt.isChecked){
            val comunicacion = GestorBT(requireActivity(), requireContext(), this)
            comunicacion.activarComoMTU()
        }
        if(binding.radioWifi.isChecked) {
            val comunicacion = GestorWF()
            comunicacion.activarComoMTU()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enviarConcentrador() {
        if(binding.radioBt.isChecked){
            val comunicacion = GestorBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext(), this)
            comunicacion.activarComoRTU()
        }
        if(binding.radioWifi.isChecked){
            val comunicacion = GestorWF()
            comunicacion.activarComoRTU(binding.txtMasterBt.text.toString())
        }
    }

    private fun enviarEmail() {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(message: String) {
        binding.recepcionBt.text = message
    }
}
