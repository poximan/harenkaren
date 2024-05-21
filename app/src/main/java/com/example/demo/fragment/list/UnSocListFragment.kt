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

        unSocViewModel.readAsynConFK(args.idRecorrido) {
            val texto: String = when (it.size) {
                0 -> "Aun no has agregado ningun registro, y por lo tanto la lista esta vacia. Hacé click en (+) para agregarlo"
                1 -> "Ahora hay un solo registro dado de alta. Cuando agregues mas, notaras la lista. Hace click en el registro" +
                        " existente para ver su detalle"

                else -> {
                    "Hace click en la fila que representa el registro de interes, para poder continuar a su detalle. " +
                            "Allí podrás revisar los valores definidos durante la observacion de esa unidad social"
                }
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Registros")
            builder.setMessage(
                "Esta pantalla muestra una lista de los registros, " +
                        "en donde cada registro esta representado por una fila.\n$texto"
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