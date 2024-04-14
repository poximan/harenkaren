package com.example.demo.export

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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

        binding.emailBtn.setOnClickListener { enviarMedioExterno() }
        binding.bluetoothBtn.setOnClickListener { enviarConcentrador() }

        binding.radioBt.setOnClickListener { clickRadio() }
        binding.radioWifi.setOnClickListener { clickRadio() }

        return binding.root
    }

    private fun clickRadio() {

        if(binding.radioBt.isChecked){
            binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT
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
            binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
            binding.bluetoothBtn.text = "enviar por WF"
            binding.idMasterBt.text = "IP destino"
        }
    }

    @SuppressLint("MissingPermission")
    private fun enviarConcentrador() {

        var listaEntidadesPlanas = runBlocking {
            getEntidades()
        }

        val listaParcel = parcelarLista(listaEntidadesPlanas)

        if(binding.radioBt.isChecked){
            val comunicacion = ExportarBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())
            comunicacion.activarComoRTU(listaParcel)
        }
        if(binding.radioWifi.isChecked){
            val comunicacion = ExportarWF()
            comunicacion.activarComoRTU(listaParcel, binding.txtMasterBt.text.toString())
        }
    }

    private fun enviarMedioExterno() {

        var listaEntidadesPlanas = runBlocking {
            getEntidades()
        }

        var datosEMAIL = CreadorCSV().empaquetarCSV(requireContext(), listaEntidadesPlanas)

        val destinatarios = arrayOf("poxi_man@yahoo.com")
        val asunto = "respaldo censo"
        val cuerpo = "este es un respaldo de los datos contenidos en la app"

        EmailSender.sendEmail(destinatarios, asunto, cuerpo, datosEMAIL, requireContext())
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

    private fun parcelarLista(entidades: List<EntidadesPlanas>): ArrayList<Parcelable> {
        val listaParcelable = ArrayList<Parcelable>()
        for (entidad in entidades) {
            val parcel = Parcel.obtain()
            entidad.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            listaParcelable.add(EntidadesPlanas.createFromParcel(parcel))
            parcel.recycle()
        }
        return listaParcelable
    }
}
