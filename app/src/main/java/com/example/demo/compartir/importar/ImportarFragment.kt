package com.example.demo.compartir.importar

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.activity.MainActivity
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.databinding.FragmentImportarBinding
import com.example.demo.model.EntidadesPlanas
import com.example.demo.servicios.ETL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ImportarFragment : Fragment(), RegistroDistribuible, ListaImportable {

    companion object {
        const val TAG = "compartir"
    }

    private var _binding: FragmentImportarBinding? = null
    private val binding get() = _binding!!

    private lateinit var comBT: ImportarBT
    private lateinit var comWF: ImportarWF

    private lateinit var leerCSV: LeerCSV

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportarBinding.inflate(inflater, container, false)

        binding.radioBt.setOnClickListener { clickBT() }
        binding.radioWifi.setOnClickListener { clickWF() }
        binding.importarBtn.setOnClickListener { agregarCSV() }

        comBT = ImportarBT(requireContext(), this)
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

    private fun clickBT() {
        apagarServNSD()

        binding.idMasterBt.text = "el alias es:"
        binding.recepcionBt.text = "esperando datos desde remota"

        comBT.activarComoMTU()
    }

    private fun clickWF() {

        comWF.descubrir()
        val port = comWF.activarComoMTU()

        binding.idMasterBt.text = "En DESTINATARIOS buscame como ${comWF.miNombre()}:$port"
        binding.recepcionBt.text = "esperando datos desde remota"
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

    private fun desmapearLista(mapas: ArrayList<Map<String, String>>): List<EntidadesPlanas> {
        val listaEntidades = mutableListOf<EntidadesPlanas>()
        for (map in mapas) {

            // puede ser una cadena vacia. no es obligatorio un string descriptivo
            val diaId = MainActivity.obtenerUUID("")
            val recorrId = MainActivity.obtenerUUID("")
            val unidSocId = MainActivity.obtenerUUID("")

            val etl = ETL(requireContext())

            val fechaTransformada = etl.transformarFecha(map["fecha"]!!)
            val lat0 = etl.transformarLatLon(map["lat0"])
            val lon0 = etl.transformarLatLon(map["lon0"])
            val ptoObs = etl.transformarPtoObservacion("")
            val ctxSocial = etl.transformarCtxSocial(map["referencia"]!!)
            val sustrato = etl.transformarSustrato("")

            val entidadPlanta = EntidadesPlanas(
                "cel_no_aplica", diaId, map["orden"]!!.toInt(), fechaTransformada,
                recorrId, diaId, map["orden"]!!.toInt(), "observador_desc", fechaTransformada, fechaTransformada,
                lat0, lon0, lat0, lon0, map["playa"]!!, "meteo_desc", "marea_desc",
                unidSocId, recorrId, map["orden"]!!.toInt(), ptoObs, ctxSocial, sustrato,
                map["machosContados"]!!.toInt(), 0, map["hembrasContadas"]!!.toInt(), 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                fechaTransformada, lat0, lon0, "foto_desc", map["tipo"]!!
            )
            listaEntidades.add(entidadPlanta)
        }
        return listaEntidades
    }

    private fun insertarEntidades(
        listaArribo: List<EntidadesPlanas>,
        mapContador: Map<String, Int>
    ) {
        val viewModelScope = viewLifecycleOwner.lifecycleScope
        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background

                val bd = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)

                val diasInsert = bd.diaDao().insertarDesnormalizado(listaArribo)
                val recorrInsert = bd.recorrDao().insertarDesnormalizado(listaArribo)
                val unSocInsert = bd.unSocDao().insertarDesnormalizado(listaArribo)

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
        val texto: String = "Los registros contabilizados en la importacion fueron $lote," +
                " desglosados en:\n${mapContador["dias"]} dias, ${mapContador["recorr"]} recorridos y ${mapContador["unidsoc"]} unidades sociales.\n" +
                "De ese lote, se insertaron a esta BD:\n$diasInsert dias, $recorrInsert recorridos y $unSocInsert unidades sociales."

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Nuevos registros")
        builder.setMessage(texto)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
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
        binding.recepcionBt.text = "Legaron desde dispositivo remoto $mapContador"
    }

    // callback del importador de csv's
    override fun onPelosReceived(message: ArrayList<Map<String, String>>) {
        val listaEntidadesPlanas = desmapearLista(message)
        val mapContador = sumarEntidades(listaEntidadesPlanas)
        Log.i(
            TAG,
            "distribuidos en ${mapContador["dias"]} dias, ${mapContador["recorr"]} recorridos y ${mapContador["unidsoc"]} unidades sociales"
        )
        insertarEntidades(listaEntidadesPlanas, mapContador)
    }
}
