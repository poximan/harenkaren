package com.example.demo.export

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentExportarBinding
import com.example.demo.fragment.messaging.EmailSender
import com.example.demo.model.EntidadesPlanas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ExportarFragment : Fragment() {

    private var _binding: FragmentExportarBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportarBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarEmail() }
        binding.bluetoothBtn.setOnClickListener { enviarConcentrador() }

        binding.radioBt.setOnClickListener { clickRadio() }
        binding.radioWifi.setOnClickListener { clickRadio() }

        return binding.root
    }

    private fun clickRadio() {

        if(binding.radioBt.isChecked){
            binding.bluetoothBtn.text = "enviar por BT"
            binding.idMasterBt.text = "ID bluetooth"

            val comunicacion = ExportarBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())

            val datosConcatenados = StringBuilder()
            for (elemento in comunicacion.obtenerDispositivosEmparejados()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                datosConcatenados.append("MAC: ${elemento.address}, Alias: ${elemento.name}\n")
            }

            binding.recepcionBt.text = datosConcatenados.toString()
        }
        if(binding.radioWifi.isChecked) {
            binding.bluetoothBtn.text = "enviar por WF"
            binding.idMasterBt.text = "IP destino"
        }
    }

    @SuppressLint("MissingPermission")
    private fun enviarConcentrador() {

        val listaParcel = runBlocking {
            getBD()
        }

        if(binding.radioBt.isChecked){
            val comunicacion = ExportarBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())
            comunicacion.activarComoRTU(listaParcel)
        }
        if(binding.radioWifi.isChecked){
            val comunicacion = ExportarWF()
            comunicacion.activarComoRTU(listaParcel, binding.txtMasterBt.text.toString())
        }
    }

    private fun enviarEmail() {

        val listaEntidades = runBlocking {
            getEntidades()
        }
        var datosEMAIL = CreadorCSV().empaquetarCSV(requireContext(),listaEntidades)

        val destinatarios = arrayOf("poxi_man@yahoo.com")
        val asunto = "Asunto del correo electrónico"
        val cuerpo = "Este es el contenido del correo electrónico."

        EmailSender.sendEmail(destinatarios, asunto, cuerpo, requireContext())
    }

    private suspend fun getEntidades(): List<EntidadesPlanas> {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        return withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
            val dao = HarenKarenRoomDatabase
                .getDatabase(requireActivity().application, viewModelScope)
                .unSocDao()
            return@withContext dao.getUnSocDesnormalizado()
        }
    }

    private suspend fun getBD(): ArrayList<Parcelable> {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        return withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
            val dao = HarenKarenRoomDatabase
                .getDatabase(requireActivity().application, viewModelScope)
                .unSocDao()
            val unsocDesnormalizado = dao.getUnSocDesnormalizado()
            return@withContext dao.parcelarLista(unsocDesnormalizado)
        }
    }
}
