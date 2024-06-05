package com.example.demo.fragment.list

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.DevFragment
import com.example.demo.R
import com.example.demo.adapter.DiaListAdapter
import com.example.demo.databinding.FragmentDiaListBinding
import com.example.demo.model.Dia
import com.example.demo.servicios.GestorUUID
import com.example.demo.viewModel.DiaViewModel
import java.text.SimpleDateFormat
import java.util.Date

class DiaListFragment : Fragment(), DiaListAdapter.OnDiaClickListener {

    private val diaViewModel: DiaViewModel by navGraphViewModels(R.id.app_navigation)

    private var _binding: FragmentDiaListBinding? = null
    private val binding get() = _binding!!
    private lateinit var diaList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentDiaListBinding.inflate(inflater, container, false)

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.newRecorrButton.setOnClickListener { nvoDia() }

        diaList = binding.listDia
        loadFullList()

        return binding.root
    }

    override fun onItemClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToRecorrListAction(dia.id)
        findNavController().navigate(action)
    }

    override fun onIconClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToGrafDdeDiaListAction(dia)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nvoDia() {
        val currentDate = getCurrentDate()
        confirmarDia(currentDate)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filtro_ejecutar -> {
                filtrar()
                true
            }
            R.id.filtro_limpiar -> {
                loadFullList()
                true
            }
            R.id.ayuda -> {
                mostrarAyuda()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarAyuda() {

        val texto: String = when (diaViewModel.allDia.value?.size) {
            0 -> "Aun no has agregado ningun dia, y por lo tanto la lista esta vacia. Hacé click en (+) para agregarlo"
            1 -> "Ahora hay un solo dia dado de alta. Cuando agregues mas, notaras la lista. Hace click en el dia" +
                    " existente para administrar sus recorridos"

            else -> {
                "Hace click en la fila que representa el dia de interes, para poder continuar a su detalle. " +
                        "Allí podrás adminstrar los recorridos asociados a la fecha que seleccionaste"
            }
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Fechas")
        builder.setMessage(
            "Esta pantalla representa una lista de dias, " +
                    "en donde cada dia esta representado por una fila.\n$texto"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun filtrar() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            activity!!,
            { _, year, monthOfYear, dayOfMonth ->
                val dateSelected = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                loadListWithDate(dateSelected)
            }, year, month, day
        )
        dpd.show()
    }

    private fun loadFullList() {

        val diaAdapter = DiaListAdapter(this)
        diaList.adapter = diaAdapter

        diaViewModel.allDia
            .observe(
                viewLifecycleOwner
            ) { elem ->
                elem?.let {
                    val sortedList = it.sortedBy { dia -> dia.fecha }
                    diaAdapter.setDia(sortedList)
                }
            }
    }

    private fun loadListWithDate(date: String) {

        val diaAdapter = DiaListAdapter(this)
        diaList.adapter = diaAdapter

        diaViewModel.allDia
            .observe(
                viewLifecycleOwner
            ) { elem ->
                elem?.let {
                    val filteredList = remove(elem, date)
                    val sortedList = filteredList.sortedBy { dia -> dia.orden }
                    diaAdapter.setDia(sortedList)
                }
            }
    }

    private fun remove(arr: List<Dia>, target: String): List<Dia> {
        val result: MutableList<Dia> = ArrayList()

        for (elem in arr) {
            if (elem.fecha == target) {
                result.add(elem)
            }
        }
        return result
    }

    private fun confirmarDia(currentDate: String){
        val dia = dataDesdeIU(currentDate)
        diaViewModel.insert(dia)
        Toast.makeText(activity, "Dia agregado correctamente", Toast.LENGTH_LONG).show()
    }

    private fun dataDesdeIU(timestamp: String): Dia {
        val celularId = GestorUUID.obtenerAndroidID()
        val uuid = DevFragment.UUID_NULO
        return Dia(celularId, uuid, 0, timestamp)
    }

    private fun getCurrentDate(): String {
        val formato = requireContext().resources.getString(R.string.formato_dia)
        return SimpleDateFormat(formato).format(Date())
    }
}