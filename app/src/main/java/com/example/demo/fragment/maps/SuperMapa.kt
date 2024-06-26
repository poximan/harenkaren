package com.example.demo.fragment.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.demo.R
import com.example.demo.activity.HomeActivity
import com.example.demo.dao.UnSocDAO
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.UnidSocial
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import kotlin.math.abs

abstract class SuperMapa: Fragment() {

    protected lateinit var unSocList: List<UnidSocial>
    private lateinit var unSocDAO: UnSocDAO

    private lateinit var chkMapaCalor: CheckBox

    protected var anios: MutableList<String> = mutableListOf()
    protected lateinit var filtroAnio: Spinner

    protected var selectedRadioButton: RadioButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_osm, container, false)
        filtroAnio = view.findViewById(R.id.filtroAnio)

        val diaDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .diaDao()

        CoroutineScope(Dispatchers.IO).launch {

            val listaDeStrings: List<String> = diaDAO.getAnios().map { it.toString() }
            anios.add(getString(R.string.osm_anios))
            anios.addAll(listaDeStrings)

            withContext(Dispatchers.Main) {
                // Configurar el Spinner
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, anios)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                filtroAnio.adapter = adapter
            }
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chkMapaCalor = view.findViewById(R.id.chk_mapacalor)
        chkMapaCalor.setOnCheckedChangeListener { _, _ ->
            lanzarEventoSpinner(view)
        }

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        configurarFiltroAnio()
    }

    private fun lanzarEventoSpinner(view: View) {

        var defaultPosition: Int = if (filtroAnio.selectedItemPosition < 0)
            0
        else
            filtroAnio.selectedItemPosition

        filtroAnio.setSelection(defaultPosition)

        filtroAnio.onItemSelectedListener?.onItemSelected(
            filtroAnio,
            view,
            defaultPosition,
            filtroAnio.getItemIdAtPosition(defaultPosition)
        )
    }

    internal fun configurarFiltroAnio() {

        filtroAnio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    val anioSeleccionado = anios[position].toInt()
                    getInvolucrados(anioSeleccionado) {
                        unSocList = it
                        cambiarMenuLateral()
                        resolverVisibilidad()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.osm_elegirAnio),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    abstract fun resolverVisibilidad()

    private fun cambiarMenuLateral() {

        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_map_categorias)

        val categorias = outerJoinCategorias(unSocList)

        for (i in categorias.indices) {
            val categoria = categorias[i]

            // Crear un nuevo MenuItem
            val menuItem = navigationView.menu.add(Menu.NONE, i, Menu.NONE, null)

            // Crear un RadioButton para este MenuItem
            val radioButton = layoutInflater.inflate(R.layout.item_categorias, null) as RadioButton
            radioButton.text = categoria

            // Escuchar clics en el RadioButton
            radioButton.setOnClickListener {
                selectedRadioButton?.isChecked = false
                selectedRadioButton = radioButton
                radioButton.isChecked = true
                resolverVisibilidad()
            }
            // Asignar el RadioButton como actionView del MenuItem
            menuItem.actionView = radioButton
        }

        // Notificar cambios en el menú para que se actualice
        navigationView.invalidate()
    }

    /**
     * de todos los contadores de categorias que tiene una unidad social, frecuentemente es necesario
     * operar sobre aquellos que no sean nulos. es decir si una unidad social tuviera los contadores
     * hembras=0, crias=1, machoPeriferico=3, interesara la lista [crias,machoPeriferico]
     * ahora bien, dada una lista esto se complejiza porque cada unidad social tendra su propio set
     * de categorias no nulas. haciendo una analogia con SQL, esta funcion realiza un OUTER JOIN de
     * categorias sobre todos las unidades sociales de una lista.
     *
     * @return la lista mas abarcativa de categorias no nulas (para al menos un caso)
     */
    private fun outerJoinCategorias(unSocList: List<UnidSocial>): List<String> {
        val combinedContadores = mutableSetOf<String>()
        for (unidSocial in unSocList) {
            combinedContadores.addAll(unidSocial.getContadoresNoNulos())
        }
        return combinedContadores.toList().reversed()
    }

    internal fun getInvolucrados(anio: Int, callback: (List<UnidSocial>) -> Unit) {
        var unSocList: List<UnidSocial>
        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            unSocList =
                unSocDAO.getAllPorAnio(anio.toString())
                    .sortedWith(compareBy({ it.recorrId }, { it.orden }))

            withContext(Dispatchers.Main) {
                callback(unSocList)
            }
        }
    }

    internal fun puntoMedioPosiciones(unSocList: List<UnidSocial>): GeoPoint {
        val minLatitud = unSocList.minOf { it.latitud }
        val maxLatitud = unSocList.maxOf { it.latitud }
        val minLongitud = unSocList.minOf { it.longitud }
        val maxLongitud = unSocList.maxOf { it.longitud }

        // Calcular los puntos medios respecto a los valores extremos
        val puntoMedioLatitud = (minLatitud + maxLatitud) / 2.0
        val puntoMedioLongitud = (minLongitud + maxLongitud) / 2.0

        val altitud = -1.9481 * abs(minLatitud - maxLatitud) + 12.0
        /*
        para 0.78 dif lat --> 8 altitud
        para 1.55 dif lat --> 6.5 altitud
         */
        return GeoPoint(puntoMedioLatitud, puntoMedioLongitud, altitud)
    }
}
