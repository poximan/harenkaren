package com.example.demo.fragment.list

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.CensoListAdapter
import com.example.demo.databinding.FragmentCensoListBinding
import com.example.demo.model.Censo
import com.example.demo.viewModel.CensoViewModel

class CensoListFragment : Fragment(), CensoListAdapter.OnCensoClickListener {

    private val reportViewModel: CensoViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentCensoListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentCensoListBinding.inflate(inflater, container, false)
        _binding!!.homeActionButton.setOnClickListener { goHome() }

        _binding!!.newCensoActionButton.setOnClickListener{ newCenso() }

        loadFullList()

        return binding.root
    }

    override fun onItemClick(report: Censo) {
        val action = CensoListFragmentDirections.goToCensoDetailFromMyCensoAction(report)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun newCenso() {
        findNavController().navigate(R.id.goToNewCensoAction)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return if (id == R.id.report_action_filter) {

            val dpd = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
        val reportList: RecyclerView = binding.list
        val reportAdapter = CensoListAdapter(this)
        reportList.adapter = reportAdapter

        reportViewModel.allCensos
            .observe(
                viewLifecycleOwner,
                Observer { reports ->
                    reports?.let { reportAdapter.setCensos(it) }
                }
            )

    }

    private fun loadListWithDate(date: String) {
        val reportList: RecyclerView = binding.list
        val reportAdapter = CensoListAdapter(this)
        reportList.adapter = reportAdapter

        reportViewModel.allCensos
            .observe(
                viewLifecycleOwner,
                Observer { reports ->
                    val filteredList = remove(reports, date)
                    reports?.let { reportAdapter.setCensos(filteredList) }
                }
            )
    }

    private fun remove(arr: List<Censo>, target: String): List<Censo> {
        val result: MutableList<Censo> = ArrayList()

        for (report in arr) {
            if (report.date == target) {
                result.add(report)
            }
        }
        return result
    }
}