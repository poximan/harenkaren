package phocidae.mirounga.leonina.fragment.maps

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.views.MapView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.activity.HomeActivity
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.repository.RecorrRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MapsFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var webView: WebView
    private lateinit var chkMapaCalor: CheckBox
    private lateinit var botonMenu: Button
    private lateinit var botonFechas: Button

    private var selectedRadioButton: RadioButton? = null
    private var desdeBase = ""
    private var hastaBase = ""

    private lateinit var mapota: SuperMapa

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_osm, container, false)

        webView = view.findViewById(R.id.webViewHeat)
        mapView = view.findViewById(R.id.mapView)
        chkMapaCalor = view.findViewById(R.id.chk_mapacalor)
        botonFechas = view.findViewById(R.id.boton_fechas)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cambiarMenuLateral(emptyList())
        configCheckbox()

        botonMenu = view.findViewById(R.id.boton_menu)
        botonMenu.setOnClickListener {
            (activity as? HomeActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        botonFechas.setOnClickListener { seleccionarRango() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_drawer_menu)
    }

    private fun configCheckbox() {
        chkMapaCalor.setOnCheckedChangeListener { _, ischeck ->
            if (selectedRadioButton != null) {

                if (ischeck) mapaSecundario()
                else mapaPorDefecto()

                lanzarMapa()
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

    private fun seleccionarRango() {

        showDateRangePicker { startDate, endDate ->
            desdeBase = startDate
            hastaBase = endDate

            lanzarMapa()
        }
    }

    private fun lanzarMapa() {
        getInvolucrados() {

            if (it.isNullOrEmpty())
                mensajeError()
            else {
                cambiarMenuLateral(it)
                mapota.resolverVisibilidad(it, selectedRadioButton!!.text.toString())
            }
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

    private fun getInvolucrados(callback: (List<UnidSocial>) -> Unit) {

        val desde = "$desdeBase 00:00:00"
        val hasta = "$hastaBase 23:59:59"

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
                recorrRepo.getAllPorAnio(desde, hasta, unSocDAO, requireContext())

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
        return combinedContadores.toList()
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

    private fun showDateRangePicker(onDateSelected: (String, String) -> Unit) {
        val dateFormat = getString(R.string.formato_dia)
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Dialogo para elegir fecha de inicio
        val startDatePicker = DatePickerDialog(requireContext())

        startDatePicker.setTitle(getString(R.string.hom_fecha_desde))
        startDatePicker.setOnDateSetListener { _, startYear, startMonth, startDay ->
            val startCalendar = Calendar.getInstance().apply {
                set(startYear, startMonth, startDay)
            }
            val startDate = dateFormatter.format(startCalendar.time)

            // fecha de fin con fecha mínima establecida en funcion de inicio
            val endDatePicker =
                DatePickerDialog(requireContext(), null, startYear, startMonth, startDay)
            endDatePicker.datePicker.minDate =
                startCalendar.timeInMillis // Establecer la fecha mínima

            endDatePicker.setTitle(R.string.hom_fecha_hasta)
            endDatePicker.setOnDateSetListener { _, endYear, endMonth, endDay ->
                val endCalendar = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay)
                }
                val endDate = dateFormatter.format(endCalendar.time)

                onDateSelected(startDate, endDate)
            }
            endDatePicker.show()
        }
        startDatePicker.show()
    }
}