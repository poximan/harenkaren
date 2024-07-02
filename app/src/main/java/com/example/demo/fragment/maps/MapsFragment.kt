package com.example.demo.fragment.maps

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.demo.R
import com.example.demo.activity.HomeActivity
import com.example.demo.database.HarenKarenRoomDatabase
import com.example.demo.model.UnidSocial
import com.example.demo.repository.RecorrRepository
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.views.MapView

class MapsFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var webView: WebView
    private lateinit var chkMapaCalor: CheckBox
    private lateinit var botonMenu: Button
    private var selectedRadioButton: RadioButton? = null

    private lateinit var filtroAnio: Spinner
    private var anios: MutableList<String> = mutableListOf()

    private lateinit var mapota: SuperMapa

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_osm, container, false)

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

        webView = view.findViewById(R.id.webViewHeat)
        mapView = view.findViewById(R.id.mapView)
        chkMapaCalor = view.findViewById(R.id.chk_mapacalor)
        filtroAnio = view.findViewById(R.id.filtroAnio)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cambiarMenuLateral(emptyList())
        configCheckbox(view)
        botonMenu = view.findViewById(R.id.boton_menu)
        botonMenu.setOnClickListener {
            (activity as? HomeActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        configSpinner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_drawer_menu)
    }

    private fun configCheckbox(view: View) {
        chkMapaCalor.setOnCheckedChangeListener { _, ischeck ->
            if (selectedRadioButton != null) {

                if (ischeck) mapaSecundario()
                else mapaPorDefecto()

                lanzarEventoSpinner(view)
            } else {
                chkMapaCalor.isChecked = false
                val context = requireContext()
                Toast.makeText(
                    context,
                    context.getString(R.string.osm_categoria),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        mapaPorDefecto()
    }

    private fun configSpinner() {
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
                        if (it.isNullOrEmpty())
                            mensajeError()
                        else {
                            cambiarMenuLateral(it)
                            mapota.resolverVisibilidad(it, selectedRadioButton!!.text.toString())
                        }
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

    private fun mapaPorDefecto() {
        webView.visibility = View.GONE
        mapView.visibility = View.VISIBLE
        mapota = MapOSMAdapter(mapView, requireContext())
        (mapota as MapOSMAdapter).configurar()
    }

    private fun mapaSecundario() {
        mapView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        mapota = MapCalorAdapter(webView, requireContext())
    }

    private fun getInvolucrados(anio: Int, callback: (List<UnidSocial>) -> Unit) {
        var unSocList: List<UnidSocial>
        var unSocMutante: List<UnidSocial>

        val unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .unSocDao()
        val recorrDAO = HarenKarenRoomDatabase
            .getDatabase(requireActivity().application, viewLifecycleOwner.lifecycleScope)
            .recorrDao()
        val recorrRepo = RecorrRepository(recorrDAO)

        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            unSocMutante =
                recorrRepo.getAllPorAnio(anio.toString(), unSocDAO)

            withContext(Dispatchers.Main) {
                callback(unSocMutante)
            }
        }
    }

    private fun cambiarMenuLateral(unSocList: List<UnidSocial>) {

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
                mapota.resolverVisibilidad(unSocList, selectedRadioButton!!.text.toString())
            }
            // Asignar el RadioButton como actionView del MenuItem
            menuItem.actionView = radioButton
            if (i == 0) {
                radioButton.isChecked = true
                selectedRadioButton = radioButton
            }
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

    private fun mensajeError() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.osm_noregTit))
        builder.setMessage(R.string.osm_noregMsj)

        builder.setPositiveButton(R.string.osm_noregMsjBtn) { _, _ -> }
        builder.setNegativeButton(getString(R.string.varias_volver)) { _, _ ->
            findNavController().navigateUp()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun findNavController(): NavController {
        return (activity as HomeActivity).findNavController(R.id.navHostHome)
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
}