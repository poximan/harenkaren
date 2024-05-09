package com.example.demo.compartir.importar

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentImportarBinding
import com.example.demo.model.EntidadesPlanas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportarFragment : Fragment(), RegistroDistribuible {

    companion object {
        const val TAG = "compartir"
    }

    private var _binding: FragmentImportarBinding? = null
    private val binding get() = _binding!!

    private lateinit var comBT: ImportarBT
    private lateinit var comWF: ImportarWF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportarBinding.inflate(inflater, container, false)

        binding.radioBt.setOnClickListener { clickBT() }
        binding.radioWifi.setOnClickListener { clickWF() }

        comBT = ImportarBT(requireContext(), this)
        comWF = ImportarWF(requireContext(), this)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        apagarServNSD()
    }

    private fun apagarServNSD() {
        try {
            comWF.desconectar()
        } catch (e: UninitializedPropertyAccessException) {
        }
    }

    private fun clickBT() {
        apagarServNSD()

        binding.idMasterBt.text = "el alias es:"
        binding.recepcionBt.text = "esperando datos desde remota"

        comBT.activarComoMTU()
    }

    private fun clickWF() {

        comWF.descubrir()
        comWF.activarComoMTU()

        binding.idMasterBt.text = "En DESTINATARIOS buscame como ${comWF.miNombre()}"
        binding.recepcionBt.text = "esperando datos desde remota"
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
        Log.i(TAG, "transformados a ${listaEntidadesPlanas.size} objetos desnomarlizados")
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
}
