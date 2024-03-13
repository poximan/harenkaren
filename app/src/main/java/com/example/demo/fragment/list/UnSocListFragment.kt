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
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.UnSocListAdapter
import com.example.demo.databinding.FragmentUnsocListBinding
import com.example.demo.fragment.detail.RecorrDetailFragmentArgs
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel

class UnSocListFragment : Fragment(), UnSocListAdapter.OnUnSocClickListener {

    private val reportViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
    private val args: UnSocListFragmentArgs by navArgs()

    private var _binding: FragmentUnsocListBinding? = null
    private val binding get() = _binding!!
    private lateinit var unSocList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentUnsocListBinding.inflate(inflater, container, false)
        _binding!!.homeActionButton.setOnClickListener { goHome() }

        _binding!!.newUnsocButton.setOnClickListener{ nuevaUnidadSocial() }

        unSocList = binding.list
        loadFullList()

        return binding.root
    }

    override fun onItemClick(report: UnidSocial) {
        val action = UnSocListFragmentDirections.goToUnSocDetailFromUnSocListAction(report)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nuevaUnidadSocial() {
        val action = UnSocListFragmentDirections.goToNewUnSocFromUnSocListAction(args.idRecorrido)
        findNavController().navigate(action)
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

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        reportViewModel.joinRecorrUnSoc
            .observe(
                viewLifecycleOwner
            ) { unSocList ->
                unSocList?.let { unSocAdapter.setUnSoc(it) }
            }
    }

    private fun loadListWithDate(date: String) {

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        reportViewModel.joinRecorrUnSoc
            .observe(
                viewLifecycleOwner
            ) { unSocList ->
                val filteredList = remove(unSocList, date)
                unSocList?.let { unSocAdapter.setUnSoc(filteredList) }
            }
    }

    private fun remove(arr: List<UnidSocial>, target: String): List<UnidSocial> {
        val result: MutableList<UnidSocial> = ArrayList()

        for (report in arr) {
            if (report.date == target) {
                result.add(report)
            }
        }
        return result
    }
}