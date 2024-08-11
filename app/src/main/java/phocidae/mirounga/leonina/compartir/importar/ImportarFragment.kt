package phocidae.mirounga.leonina.compartir.importar

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.databinding.FragmentImportarBinding
import phocidae.mirounga.leonina.model.EntidadesPlanas
import java.util.UUID

class ImportarFragment : Fragment(), RegistroDistribuible, ListaImportable {

    companion object {
        const val TAG = "compartir"
    }

    private var _binding: FragmentImportarBinding? = null
    private val binding get() = _binding!!

    private lateinit var comWF: ImportarWF
    private lateinit var leerCSV: LeerCSV

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportarBinding.inflate(inflater, container, false)

        binding.radioWifi.setOnClickListener { clickWF() }
        binding.importarBtn.setOnClickListener { agregarCSV() }

        comWF = ImportarWF(requireContext(), this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leerCSV = LeerCSV(requireContext(), this)
        leerCSV.registerLauncher()
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

    private fun clickWF() {

        comWF.descubrir()
        val port = comWF.activarComoMTU()

        binding.idMaster.text =
            "${requireContext().getString(R.string.imp_clickWF1)} ${comWF.miNombre()}:$port"
        binding.recepcion.text = requireContext().getString(R.string.imp_clickWF2)
    }

    private fun agregarCSV() {
        leerCSV.pickCSVFile()
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

    private fun insertarEntidades(
        listaArribo: List<EntidadesPlanas>,
        mapContador: Map<String, Int>
    ) {
        val viewModelScope = viewLifecycleOwner.lifecycleScope
        val bd = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewModelScope)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val diasInsert =
                    bd.diaDao().insertarDesnormalizado(listaArribo, this@ImportarFragment)
                val recorrInsert =
                    bd.recorrDao().insertarDesnormalizado(listaArribo, this@ImportarFragment)
                val unSocInsert =
                    bd.unSocDao().insertarDesnormalizado(listaArribo, this@ImportarFragment)

                withContext(Dispatchers.Main) {
                    mostrarResultado(
                        listaArribo.size,
                        mapContador,
                        diasInsert,
                        recorrInsert,
                        unSocInsert
                    )
                }
            }
        }
    }

    private fun mostrarResultado(
        lote: Int,
        mapContador: Map<String, Int>,
        diasInsert: Int,
        recorrInsert: Int,
        unSocInsert: Int
    ) {
        val texto: String = "${requireContext().getString(R.string.imp_mostrarResMsg1)} $lote, " +
                "${requireContext().getString(R.string.imp_mostrarResMsg2)}\n" +
                "${mapContador["dias"]} ${requireContext().getString(R.string.varias_rangofecha)}, " +
                "${mapContador["recorr"]} ${requireContext().getString(R.string.dev_recorr)} y " +
                "${mapContador["unidsoc"]} ${requireContext().getString(R.string.dev_unsoc)}.\n" +

                "${requireContext().getString(R.string.imp_mostrarResMsg3)}\n" +
                "$diasInsert ${requireContext().getString(R.string.varias_rangofecha)}, " +
                "$recorrInsert ${requireContext().getString(R.string.dev_recorr)} y " +
                "$unSocInsert ${requireContext().getString(R.string.dev_unsoc)}"

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(requireContext().getString(R.string.imp_mostrarResTit))
        builder.setMessage(texto)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigateUp()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun sumarEntidades(entidades: List<EntidadesPlanas>): Map<String, Int> {
        val sumaDias = mutableMapOf<UUID, Int>()
        val sumaRecorridos = mutableMapOf<UUID, Int>()
        val sumaUnidadesSociales = mutableMapOf<UUID, Int>()

        /*
        crea una nueva clave cada vez. si encuentra que ya existe hace +1. por lo tanto
        hay doble utilidad aqui, se sabe cuantos valores distintos hay (por la distribucion
        de las claves) y se sabe qué concentracion en cada clave (por el +1)
         */
        for (entidad in entidades) {
            sumaDias[entidad.dia_id] = (sumaDias[entidad.dia_id] ?: 0) + 1
            sumaRecorridos[entidad.recorr_id] = (sumaRecorridos[entidad.recorr_id] ?: 0) + 1
            sumaUnidadesSociales[entidad.unsoc_id] =
                (sumaUnidadesSociales[entidad.unsoc_id] ?: 0) + 1
        }

        val resultado = mutableMapOf<String, Int>()
        resultado["dias"] = sumaDias.size
        resultado["recorr"] = sumaRecorridos.size
        resultado["unidsoc"] = sumaUnidadesSociales.size

        return resultado
    }

    // callback del arribo de objetos parcelados, obtenidos desde otra app
    override fun onMessageReceived(message: ArrayList<Parcelable>) {
        val listaEntidadesPlanas = desparcelarLista(message)
        Log.i(TAG, "transformados a ${listaEntidadesPlanas.size} objetos desnomarlizados")

        val mapContador = sumarEntidades(listaEntidadesPlanas)
        Log.i(
            TAG,
            "distribuidos en ${mapContador["dias"]} dias, ${mapContador["recorr"]} recorridos y ${mapContador["unidsoc"]} unidades sociales"
        )
        insertarEntidades(listaEntidadesPlanas, mapContador)
        binding.recepcion.text =
            "${requireContext().getString(R.string.imp_onMessage)} $mapContador"
    }

    // callback del importador de csv's
    override fun onPelosReceived(message: ArrayList<Map<String, String>>) {

        val demaper = DemapFactory.crearDemap(requireContext(), message.first())
        val listaEntidadesPlanas = demaper.desmapear(message)

        val mapContador = sumarEntidades(listaEntidadesPlanas)
        Log.i(
            TAG,
            "distribuidos en ${mapContador["dias"]} dias, ${mapContador["recorr"]} recorridos y ${mapContador["unidsoc"]} unidades sociales"
        )
        insertarEntidades(listaEntidadesPlanas, mapContador)
        binding.recepcion.text = "${requireContext().getString(R.string.imp_onPelos)} $mapContador"
    }

    override fun progreso(valor: Float) {
        binding.recepcion.text =
            "${requireContext().getString(R.string.imp_progreso)} ${
                valor.toString().substringBefore(".")
            }%"
    }

    fun avanceInserts(avance: String) {
        binding.insertsbd.text =
            "${requireContext().getString(R.string.imp_insertBD)} ${avance}%"
    }
}
