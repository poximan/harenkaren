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

    private val recorrViewModel: RecorrViewModel by navGraphViewModels(R.id.navHome)
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

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.nvoRecorrButton.setOnClickListener { nvoRecorrido() }
        recorrList = binding.listRecorr
        loadFullList()

        return binding.root
    }

    override fun onItemClick(elem: Recorrido) {
        val action = RecorrListFragmentDirections.goToRecorrDetailAction(elem)
        findNavController().navigate(action)
    }

    override fun onIconClick(elem: Recorrido) {
        val action = RecorrListFragmentDirections.goToGrafDdeRecorrListAction(elem)
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
        recorrViewModel.readAsynConFK(args.idDia) {
            val texto: String = when (it.size) {
                0 -> context.getString(R.string.rec_mostrarAyuda0)
                1 -> context.getString(R.string.rec_mostrarAyuda1)
                else -> context.getString(R.string.rec_mostrarAyudaElse)
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(context.getString(R.string.rec_mostrarAyudaTit))
            builder.setMessage(
                "${context.getString(R.string.rec_mostrarAyudaMarco)}\n$texto"
            )
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
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

        val recorrAdapter = RecorrListAdapter(this)
        recorrList.adapter = recorrAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val recorrListAsync = recorrViewModel.readConFK(args.idDia)
            withContext(Dispatchers.Main) {
               recorrAdapter.setRecorrido(recorrListAsync)
            }
        }
    }

    private fun loadListWithDate(date: String) {

        val recorrAdapter = RecorrListAdapter(this)
        recorrList.adapter = recorrAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = recorrViewModel.readConFK(args.idDia)

            withContext(Dispatchers.Main) {
                val filteredList = remove(unSocListAsync, date)
                recorrAdapter.setRecorrido(filteredList)
            }
        }
    }

    private fun remove(arr: List<Recorrido>, target: String): List<Recorrido> {
        val result: MutableList<Recorrido> = ArrayList()

        for (elem in arr) {
            if (elem.fechaIni == target) {
                result.add(elem)
            }
        }
        return result
    }
}