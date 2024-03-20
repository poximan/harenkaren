package com.example.demo.fragment.list

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.DiaListAdapter
import com.example.demo.databinding.FragmentDiaListBinding
import com.example.demo.model.Dia
import com.example.demo.viewModel.DiaViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DiaListFragment : Fragment(), DiaListAdapter.OnDiaClickListener {

    private val diaViewModel: DiaViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentDiaListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentDiaListBinding.inflate(inflater, container, false)

        _binding!!.homeActionButton.setOnClickListener { goHome() }
        _binding!!.newRecorrButton.setOnClickListener{ nvoDia() }

        loadFullList()

        return binding.root
    }

    override fun onItemClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToDiaDetailAction(dia)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nvoDia() {
        val currentDate = getCurrentDate()

        diaViewModel.diaList.observe(viewLifecycleOwner) { elem ->
            val entryExists = elem.any { it.fecha == currentDate }

            if (entryExists) {
                // Si se encuentra una entrada con la fecha actual, mostrar un AlertDialog
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Dia en curso")
                builder.setMessage("Ya existe una entrada para este dia. ¿Desea continuar de todos modos?")

                builder.setPositiveButton("Sí") { _, _ ->
                    findNavController().navigate(R.id.goToNvoDiaAction)
                }
                builder.setNegativeButton("No") { _, _ -> } // no hacer nada

                val dialog = builder.create()
                dialog.show()
            } else {
                // Si no se encuentra ninguna entrada con la fecha actual, crear
                findNavController().navigate(R.id.goToNvoDiaAction)
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        return dateFormat.format(currentDate)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return if (id == R.id.filtro_ejecutar) {

            val dpd = DatePickerDialog(
                activity!!,
                { _, year, monthOfYear, dayOfMonth ->
                    val dateSelected = "" + dayOfMonth + "/" + (monthOfYear + 1)  + "/" + year
                    loadListWithDate(dateSelected)
                }, year, month, day
            )
            dpd.show()

            true
        } else return if (id == R.id.filtro_limpiar) {
            loadFullList()
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun loadFullList() {
        val diaList: RecyclerView = binding.listDia
        val diaAdapter = DiaListAdapter(this)
        diaList.adapter = diaAdapter

        diaViewModel.diaList
            .observe(
                viewLifecycleOwner
            ) { elem ->
                elem?.let { diaAdapter.setDia(it) }
            }
    }

    private fun loadListWithDate(date: String) {
        val diaList: RecyclerView = binding.listDia
        val diaAdapter = DiaListAdapter(this)
        diaList.adapter = diaAdapter

        diaViewModel.diaList
            .observe(
                viewLifecycleOwner
            ) { elem ->
                val filteredList = remove(elem, date)
                elem?.let { diaAdapter.setDia(filteredList) }
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
}