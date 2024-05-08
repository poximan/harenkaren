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

    private lateinit var comBT: ExportarBT
    private lateinit var comWF: ExportarWF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportarBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarMedioExterno() }
        binding.bluetoothBtn.setOnClickListener { enviarConcentrador() }

        binding.radioBt.setOnClickListener { clickBT() }
        binding.radioWifi.setOnClickListener { clickWF() }

        comBT = ExportarBT(requireContext(), binding.txtMasterBt.text.toString())
        comWF = ExportarWF(requireContext())

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        apagarServNSD()
    }

    private fun apagarServNSD(){
        try {
            comWF.desconectar()
        } catch (e: UninitializedPropertyAccessException){}
    }

    private fun clickBT() {
        apagarServNSD()

        binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT
        binding.bluetoothBtn.text = "enviar por BT"
        binding.idMasterBt.text = "ID bluetooth"

        val datosConcatenados = StringBuilder()
        for (elemento in comBT.obtenerDispositivosEmparejados()) {
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

        val listaParcel = prepararDatos()
        comBT.activarComoRTU(listaParcel)
    }

    private fun clickWF() {

        binding.txtMasterBt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
        binding.bluetoothBtn.text = "destinatarios"
        binding.idMasterBt.text = "IP destino"

        val listaParcel = prepararDatos()
        comWF.descubrir(listaParcel)
    }

    private fun prepararDatos():  ArrayList<Parcelable> {
        var listaEntidadesPlanas = runBlocking { getEntidades() }
        return parcelarLista(listaEntidadesPlanas)
    }

    private fun enviarMedioExterno() {

        var listaEntidadesPlanas = runBlocking { getEntidades() }
        var datosEMAIL = CreadorCSV().empaquetarCSV(requireContext(), listaEntidadesPlanas)
        val cuerpo = "este es un respaldo de los ${listaEntidadesPlanas.size} registros contenidos en la app"

        EmailSender.sendEmail(cuerpo, datosEMAIL, requireContext())
    }

    private fun enviarConcentrador() {
        comWF.levantarModal()
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