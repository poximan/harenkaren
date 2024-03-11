package com.example.demo.fragment.list

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
import com.example.demo.adapter.RecorrListAdapter
import com.example.demo.databinding.FragmentRecorrListBinding
import com.example.demo.model.Recorrido
import com.example.demo.viewModel.RecorrViewModel

class RecorrListFragment : Fragment(), RecorrListAdapter.OnRecorrClickListener {

    private val reportViewModel: RecorrViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentRecorrListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentRecorrListBinding.inflate(inflater, container, false)

        _binding!!.homeActionButton.setOnClickListener { goHome() }
        _binding!!.newUnsocButton.setOnClickListener{ newUnidadSocial() }

        loadFullList()

        return binding.root
    }

    override fun onItemClick(recorrido: Recorrido) {
        val action = RecorrListFragmentDirections.goToRecorrDetailAction(recorrido)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun newUnidadSocial() {
        findNavController().navigate(R.id.goToNewRecorrAction)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_filter_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return if (id == R.id.report_action_filter) {

            val dpd = DatePickerDialog(
                activity!!,
                { _, year, monthOfYear, dayOfMonth ->
                    val dateSelected = "" + dayOfMonth + "/" + (monthOfYear + 1)  + "/" + year
                    loadListWithDate(dateSelected)
                }, year, month, day
            )
            dpd.show()

            true
        } else return if (id == R.id.report_action_clear_filter) {
            loadFullList()
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun loadFullList() {
        val circuitoList: RecyclerView = binding.listCircuito
        val reportAdapter = RecorrListAdapter(this)
        circuitoList.adapter = reportAdapter

        reportViewModel.recorrList
            .observe(
                viewLifecycleOwner
            ) { reports ->
                reports?.let { reportAdapter.setRecorrido(it) }
            }

    }

    private fun loadListWithDate(date: String) {
        val reportList: RecyclerView = binding.listCircuito
        val reportAdapter = RecorrListAdapter(this)
        reportList.adapter = reportAdapter

        reportViewModel.recorrList
            .observe(
                viewLifecycleOwner
            ) { reports ->
                val filteredList = remove(reports, date)
                reports?.let { reportAdapter.setRecorrido(filteredList) }
            }
    }

    private fun remove(arr: List<Recorrido>, target: String): List<Recorrido> {
        val result: MutableList<Recorrido> = ArrayList()

        for (report in arr) {
            if (report.fecha == target) {
                result.add(report)
            }
        }
        return result
    }
}