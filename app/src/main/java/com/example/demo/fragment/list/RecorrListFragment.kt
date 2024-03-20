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
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.adapter.RecorrListAdapter
import com.example.demo.databinding.FragmentRecorrListBinding
import com.example.demo.model.Recorrido
import com.example.demo.viewModel.RecorrViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecorrListFragment : Fragment(), RecorrListAdapter.OnRecorrClickListener {

    private val recorrViewModel: RecorrViewModel by navGraphViewModels(R.id.app_navigation)
    private val args: RecorrListFragmentArgs by navArgs()

    private var _binding: FragmentRecorrListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recorrList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentRecorrListBinding.inflate(inflater, container, false)

        _binding!!.homeActionButton.setOnClickListener { goHome() }
        _binding!!.nvoRecorrButton.setOnClickListener{ nvoRecorrido() }

        recorrList = binding.listRecorr
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

    private fun nvoRecorrido() {
        var action = RecorrListFragmentDirections.goToNewRecorrAction(args.idDia)
        findNavController().navigate(action)
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

        val recorrAdapter = RecorrListAdapter(this)
        recorrList.adapter = recorrAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val recorrListAsync = recorrViewModel.read(args.idDia)
            withContext(Dispatchers.Main) {
                recorrListAsync.observe(
                    viewLifecycleOwner
                ) { elem ->
                    elem?.let { recorrAdapter.setRecorrido(it) }
                }
            }
        }
    }

    private fun loadListWithDate(date: String) {

        val recorrAdapter = RecorrListAdapter(this)
        recorrList.adapter = recorrAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = recorrViewModel.read(args.idDia)
            withContext(Dispatchers.Main) {
                unSocListAsync.observe(
                    viewLifecycleOwner
                ) { unSocList ->
                    val filteredList = remove(unSocList, date)
                    unSocList?.let { recorrAdapter.setRecorrido(filteredList) }
                }
            }
        }
    }

    private fun remove(arr: List<Recorrido>, target: String): List<Recorrido> {
        val result: MutableList<Recorrido> = ArrayList()

        for (elem in arr) {
            if (elem.fecha == target) {
                result.add(elem)
            }
        }
        return result
    }
}