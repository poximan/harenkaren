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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.CircuitoListAdapter
import com.example.demo.databinding.FragmentCircuitoListBinding
import com.example.demo.model.Circuito
import com.example.demo.viewModel.CircuitoViewModel

class CircuitoListFragment : Fragment(), CircuitoListAdapter.OnCircuitoClickListener {

    private val reportViewModel: CircuitoViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentCircuitoListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentCircuitoListBinding.inflate(inflater, container, false)

        _binding!!.homeActionButton.setOnClickListener { goHome() }
        _binding!!.newCensoActionButton.setOnClickListener{ newCenso() }
        _binding!!.newCensoActionButton.setOnClickListener{ newCenso() }

        loadFullList()

        return binding.root
    }

    override fun onItemClick(circuito: Circuito) {
        val action = CircuitoListFragmentDirections.goToCircuitoDetailFromMyCircuitoAction(circuito)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun newCenso() {
        findNavController().navigate(R.id.goToNewCircuitoAction)
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
        val reportAdapter = CircuitoListAdapter(this)
        circuitoList.adapter = reportAdapter

        reportViewModel.allCircuitos
            .observe(
                viewLifecycleOwner
            ) { reports ->
                reports?.let { reportAdapter.setCircuito(it) }
            }

    }

    private fun loadListWithDate(date: String) {
        val reportList: RecyclerView = binding.listCircuito
        val reportAdapter = CircuitoListAdapter(this)
        reportList.adapter = reportAdapter

        reportViewModel.allCircuitos
            .observe(
                viewLifecycleOwner
            ) { reports ->
                val filteredList = remove(reports, date)
                reports?.let { reportAdapter.setCircuito(filteredList) }
            }
    }

    private fun remove(arr: List<Circuito>, target: String): List<Circuito> {
        val result: MutableList<Circuito> = ArrayList()

        for (report in arr) {
            if (report.fecha == target) {
                result.add(report)
            }
        }
        return result
    }
}