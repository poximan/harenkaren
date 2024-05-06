package com.example.demo.compartir.exportar

import android.Manifest
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
import com.example.demo.compartir.Compartible
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
    private lateinit var comunicacion: Compartible

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportarBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarMedioExterno() }
        binding.bluetoothBtn.setOnClickListener { enviarConcentrador() }

        binding.radioBt.setOnClickListener { clickBT() }
        binding.radioWifi.setOnClickListener { clickWF() }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        apagarServNSD()
    }

    private fun apagarServNSD(){
        try {
            comunicacion.desconectar()
        } catch (e: UninitializedPropertyAccessException){}
    }

    private fun clickBT() {
        apagarServNSD()

        binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT
        binding.bluetoothBtn.text = "enviar por BT"
        binding.idMasterBt.text = "ID bluetooth"

        comunicacion =
            ExportarBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())

        val com = (comunicacion as ExportarBT)

        val datosConcatenados = StringBuilder()
        for (elemento in com.obtenerDispositivosEmparejados()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            datosConcatenados.append("MAC: ${elemento.address}, Alias: ${elemento.name}\n")
        }
        binding.recepcionBt.text = datosConcatenados.toString()
    }

    private fun clickWF() {

        binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
        binding.bluetoothBtn.text = "enviar por WF"
        binding.idMasterBt.text = "IP destino"

        comunicacion = ExportarWF(requireContext())
    }

    private fun enviarConcentrador() {

        var listaEntidadesPlanas = runBlocking {
            getEntidades()
        }

        val listaParcel = parcelarLista(listaEntidadesPlanas)

        if(binding.radioBt.isChecked){
            comunicacion =
                ExportarBT(binding.txtMasterBt.text.toString(), requireActivity(), requireContext())

            val com = (comunicacion as ExportarBT)
            com.activarComoRTU(listaParcel)
        }
        if(binding.radioWifi.isChecked){
            val com = (comunicacion as ExportarWF)
            com.descubrir()
            com.levantarModal()
            //com.activarComoRTU(listaParcel, binding.txtMasterBt.text.toString())
        }
    }

    private fun enviarMedioExterno() {

        var listaEntidadesPlanas = runBlocking {
            getEntidades()
        }

        var datosEMAIL = CreadorCSV().empaquetarCSV(requireContext(), listaEntidadesPlanas)

        val destinatarios = arrayOf("poxi_man@yahoo.com")
        val cuerpo = "este es un respaldo de los ${listaEntidadesPlanas.size} registros contenidos en la app"

        EmailSender.sendEmail(destinatarios, cuerpo, datosEMAIL, requireContext())
    }

    private suspend fun getEntidades(): List<EntidadesPlanas> {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        return withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
            val dao =
                HarenKarenRoomDatabase.getDatabase(requireActivity().application, viewModelScope)
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