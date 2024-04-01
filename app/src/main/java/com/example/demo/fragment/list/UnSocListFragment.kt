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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.UnSocListAdapter
import com.example.demo.databinding.FragmentUnsocListBinding
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnSocListFragment : Fragment(), UnSocListAdapter.OnUnSocClickListener {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
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
        _binding!!.newUnsocButton.setOnClickListener { nuevaUnidadSocial() }

        unSocList = binding.listUnSoc
        loadFullList()

        return binding.root
    }

    override fun onItemClick(elem: UnidSocial) {
        val action = UnSocListFragmentDirections.goToUnSocDetailFromUnSocListAction(elem)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nuevaUnidadSocial() {

        var action = UnSocListFragmentDirections.goToNewUnSocFromUnSocListAction(args.idRecorrido)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return if (id == R.id.filtro_ejecutar) {

            val dpd = DatePickerDialog(
                activity!!,
                { _, _, monthOfYear, dayOfMonth ->
                    val dateSelected = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
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

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
            withContext(Dispatchers.Main) {
                unSocListAsync.observe(
                    viewLifecycleOwner
                ) { elem ->
                    elem?.let { unSocAdapter.setUnSoc(it) }
                }
            }
        }
    }

    private fun loadListWithDate(date: String) {

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
            withContext(Dispatchers.Main) {
                unSocListAsync.observe(
                    viewLifecycleOwner
                ) { unSocList ->
                    val filteredList = remove(unSocList, date)
                    unSocList?.let { unSocAdapter.setUnSoc(filteredList) }
                }
            }
        }
    }

    private fun remove(arr: List<UnidSocial>, target: String): List<UnidSocial> {
        val result: MutableList<UnidSocial> = ArrayList()

        for (elem in arr) {
            if (elem.date == target) {
                result.add(elem)
            }
        }
        return result
    }
}