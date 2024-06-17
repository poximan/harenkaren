package com.example.demo.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.DiaListAdapter
import com.example.demo.database.DevFragment
import com.example.demo.databinding.FragmentDiaListBinding
import com.example.demo.model.Dia
import com.example.demo.servicios.GestorUUID
import com.example.demo.viewModel.DiaViewModel
import java.text.SimpleDateFormat
import java.util.Date

class DiaListFragment : SuperList(), DiaListAdapter.OnDiaClickListener {

    private val diaViewModel: DiaViewModel by navGraphViewModels(R.id.navHome)

    private var _binding: FragmentDiaListBinding? = null
    private val binding get() = _binding!!
    private var diaList: RecyclerView? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        diaList = null
    }

    override fun onItemClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToRecorrListAction(dia.id)
        findNavController().navigate(action)
    }

    override fun onIconClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToGrafDdeDiaListAction(dia)
        findNavController().navigate(action)
    }

    override fun onDeleteClick(dia: Dia) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dia_onDeleteTit))
        builder.setMessage(getString(R.string.dia_onDeleteMsg))
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            diaViewModel.delete(dia)
            loadFullList() // Refresh the list after deletion
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nvoDia() {
        val currentDate = getCurrentDate()
        val existe = diaViewModel.allDia.value?.filter { it.fecha == currentDate }

        if (existe.isNullOrEmpty())
            confirmarDia(currentDate)
        else {
            val context = requireContext()
            Toast.makeText(context, context.getString(R.string.dia_existe), Toast.LENGTH_LONG)
                .show()
        }
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

        val context = requireContext()
        val texto: String = when (diaViewModel.allDia.value?.size) {
            0 -> context.getString(R.string.dia_mostrarAyuda0)
            1 -> context.getString(R.string.dia_mostrarAyuda1)
            else -> context.getString(R.string.dia_mostrarAyudaElse)
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(context.getString(R.string.dia_mostrarAyudaTit))
        builder.setMessage(
            "${context.getString(R.string.dia_mostrarAyudaMarco)}\n$texto"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun loadFullList() {

        val diaAdapter = DiaListAdapter(this)
        diaList!!.adapter = diaAdapter

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

    override fun loadListWithDate(date: String) {

        val diaAdapter = DiaListAdapter(this)
        diaList!!.adapter = diaAdapter

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

    private fun confirmarDia(currentDate: String) {
        val dia = dataDesdeIU(currentDate)
        diaViewModel.insert(dia)

        val context = requireContext()
        Toast.makeText(context, context.getString(R.string.dia_confirmar), Toast.LENGTH_LONG).show()
    }

    private fun dataDesdeIU(timestamp: String): Dia {
        val celularId = GestorUUID.obtenerAndroidID()
        val uuid = DevFragment.UUID_NULO
        return Dia(celularId, uuid, 0, fecha = timestamp)
    }

    private fun getCurrentDate(): String {
        val formato = requireContext().resources.getString(R.string.formato_dia)
        return SimpleDateFormat(formato).format(Date())
    }
}