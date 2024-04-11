package com.example.demo.export

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentImportarBinding
import com.example.demo.model.EntidadesPlanas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration

class ImportarFragment : Fragment(), RegistroDistribuible {

    private var _binding: FragmentImportarBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportarBinding.inflate(inflater, container, false)

        binding.bluetoothBtn.setOnClickListener { recibirDatos() }

        binding.radioBt.setOnClickListener { clickRadio() }
        binding.radioWifi.setOnClickListener { clickRadio() }

        return binding.root
    }

    private fun clickRadio() {

        if(binding.radioBt.isChecked) {
            binding.bluetoothBtn.text = "escuchar por BT"
            binding.idMasterBt.text = "el alias es:"
        }
        if(binding.radioWifi.isChecked) {
            val direccionIpWifi = obtenerDireccionIpWifi()
            binding.bluetoothBtn.text = "escuchar por WF"
            binding.idMasterBt.text = "mi IP es $direccionIpWifi:"
        }

        binding.recepcionBt.text = "esperando datos desde remota"
    }

    private fun recibirDatos() {
        if(binding.radioBt.isChecked){
            val comunicacion = ImportarBT(requireContext(), this)
            comunicacion.activarComoMTU()
        }
        if(binding.radioWifi.isChecked) {
            val comunicacion = ImportarWF(this)
            comunicacion.activarComoMTU()
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

    private fun desparcelarLista(parcelables: ArrayList<Parcelable>): List<EntidadesPlanas> {
        val listaEntidades = mutableListOf<EntidadesPlanas>()
        for (parcelable in parcelables) {
            if (parcelable is EntidadesPlanas) {
                listaEntidades.add(parcelable)
            }
        }
        return listaEntidades
    }

    override fun onMessageReceived(lista: ArrayList<Parcelable>) {

        val listaEntidadesPlanas = desparcelarLista(lista)
        binding.recepcionBt.text = listaEntidadesPlanas.toString()
    }

    private fun obtenerDireccionIpWifi(): String? {
        try {
            val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val interfaz: NetworkInterface = interfaces.nextElement()
                val direcciones: Enumeration<InetAddress> = interfaz.inetAddresses
                while (direcciones.hasMoreElements()) {
                    val direccion: InetAddress = direcciones.nextElement()
                    if (!direccion.isLoopbackAddress && direccion is Inet4Address && interfaz.displayName.startsWith("wlan")) {
                        return direccion.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun extraerDireccionIPv4(direccion: String): String? {
        val patron = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}"
        val coincidencias = Regex(patron).findAll(direccion)
        for (coincidencia in coincidencias) {
            val ip = coincidencia.value
            // Verificar si es una dirección IPv4 válida
            if (ip.matches("\b(?:\\d{1,3}\\.){3}\\d{1,3}\b".toRegex())) {
                return ip
            }
        }
        return null
    }

}
