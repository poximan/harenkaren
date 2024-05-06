package com.example.demo.compartir.importar

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.compartir.exportar.Compartible
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentImportarBinding
import com.example.demo.model.EntidadesPlanas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration

class ImportarFragment : Fragment(), RegistroDistribuible {

    private var _binding: FragmentImportarBinding? = null
    private val binding get() = _binding!!
    private lateinit var comunicacion: Compartible

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportarBinding.inflate(inflater, container, false)

        binding.bluetoothBtn.setOnClickListener { recibirDatos() }

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

        binding.bluetoothBtn.text = "escuchar por BT"
        binding.idMasterBt.text = "el alias es:"
        binding.recepcionBt.text = "esperando datos desde remota"
    }

    private fun clickWF() {
        val direccionIpWifi = obtenerDireccionIpWifi()
        binding.bluetoothBtn.text = "escuchar por WF"
        binding.idMasterBt.text = "mi IP es $direccionIpWifi:"

        comunicacion = ImportarWF(requireContext(),this)
        binding.recepcionBt.text = "esperando datos desde remota"
    }

    private fun recibirDatos() {
        if(binding.radioBt.isChecked){
            comunicacion = ImportarBT(requireContext(), this)

            val com = (comunicacion as ImportarBT)
            com.activarComoMTU()
        }
        if(binding.radioWifi.isChecked) {
            (comunicacion as ImportarWF).descubrir()
            //com.activarComoMTU()
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
        insertarEntidades(listaEntidadesPlanas)
        binding.recepcionBt.text = listaEntidadesPlanas.toString()
    }

    private fun insertarEntidades(listaEntidadesPlanas: List<EntidadesPlanas>) {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope
        val idMapRecorr = mutableMapOf<Int, Int>()
        val idMapUnSoc = mutableMapOf<Int, Int>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background

                val bd = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)

                bd.diaDao().insertarDesnormalizado(listaEntidadesPlanas)
                bd.recorrDao().insertarDesnormalizado(listaEntidadesPlanas, idMapRecorr)
                bd.unSocDao().insertarDesnormalizado(listaEntidadesPlanas, idMapUnSoc)
            }
        }
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
}
