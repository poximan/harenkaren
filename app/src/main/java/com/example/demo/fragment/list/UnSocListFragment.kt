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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.newUnsocButton.setOnClickListener { nuevaUnidadSocial() }
        binding.cambiarActionButton.setOnClickListener { cambiarVista() }

        unSocList = binding.listUnSoc
        val layoutManager = LinearLayoutManager(requireContext())
        unSocList.layoutManager = layoutManager
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        val dividerItemDecoration =
            DividerItemDecoration(unSocList.context, layoutManager.orientation)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        unSocList.addItemDecoration(dividerItemDecoration)

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

    private fun cambiarVista() {
        var action = UnSocListFragmentDirections.goToModoGrafico(args.idRecorrido)
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
        unSocViewModel.readAsynConFK(args.idRecorrido) {
            val texto: String = when (it.size) {
                0 -> context.getString(R.string.soc_mostrarAyuda0)
                1 -> context.getString(R.string.soc_mostrarAyuda1)
                else -> context.getString(R.string.soc_mostrarAyudaElse)
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(context.getString(R.string.soc_mostrarAyudaTit))
            builder.setMessage(
                "${context.getString(R.string.soc_mostrarAyudaMarco)}\n$texto"
            )

            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
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

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
            withContext(Dispatchers.Main) {
                unSocAdapter.setUnSoc(unSocListAsync)
            }
        }
    }

    private fun loadListWithDate(date: String) {

        val unSocAdapter = UnSocListAdapter(this)
        unSocList.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)

            withContext(Dispatchers.Main) {
                val filteredList = remove(unSocListAsync, date)
                unSocAdapter.setUnSoc(filteredList)
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