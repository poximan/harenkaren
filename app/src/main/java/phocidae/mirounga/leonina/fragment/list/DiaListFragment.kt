package phocidae.mirounga.leonina.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.adapter.DiaListAdapter
import phocidae.mirounga.leonina.databinding.FragmentDiaListBinding
import phocidae.mirounga.leonina.fragment.DevFragment
import phocidae.mirounga.leonina.model.Dia
import phocidae.mirounga.leonina.servicios.GestorUUID
import phocidae.mirounga.leonina.viewModel.DiaViewModel

class DiaListFragment : SuperList(), DiaListAdapter.OnDiaClickListener {

    private val diaViewModel: DiaViewModel by navGraphViewModels(R.id.navHome)
    private val args: DiaListFragmentArgs by navArgs()

    private var _binding: FragmentDiaListBinding? = null
    private val binding get() = _binding!!
    private var diaList: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentDiaListBinding.inflate(inflater, container, false)
        diaList = binding.listDia

        val anio = getCurrentDate().split("-")[0]
        val rotateAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_animation)

        if (args.anio == anio.toInt()) {
            binding.newRecorrButton.setOnClickListener { nvoDia() }
            binding.newRecorrButton.clearAnimation()
        } else {
            binding.newRecorrButton.setOnClickListener { noMasDia(anio) }
            binding.newRecorrButton.startAnimation(rotateAnimation)
        }
        binding.homeActionButton.setOnClickListener { goHome() }

        loadFullList()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        diaList = null
    }

    override fun onItemClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToRecorrListAction(dia.id)
        findNavController().navigate(action)
    }

    override fun onIconClick(dia: Dia) {
        val action = DiaListFragmentDirections.goToGrafDdeDiaListAction(dia)
        findNavController().navigate(action)
    }

    override fun onDeleteClick(dia: Dia) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dia_onDeleteTit))
        builder.setMessage(getString(R.string.dia_onDeleteMsg))
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            diaViewModel.delete(dia, this::loadFullList)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nvoDia() {
        val currentDate = getCurrentDate()
        diaViewModel.getDias(args.anio) { diasList ->
            val existe = diasList.filter { it.fecha == currentDate }

            if (existe.isNullOrEmpty())
                confirmarDia(currentDate)
            else {
                val context = requireContext()
                Toast.makeText(context, context.getString(R.string.dia_existe), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun noMasDia(diaHoy: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dia_noDiaTit))
        builder.setMessage(getString(R.string.dia_noDiaMsg1) + " (" + diaHoy + ") " + getString(R.string.dia_noDiaMsg2))
        builder.setPositiveButton(android.R.string.ok, null)
        builder.show()
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

        diaViewModel.getDias(args.anio) { diasList ->

            val texto: String = when (diasList.size) {
                0 -> context.getString(R.string.dia_mostrarAyuda0)
                1 -> context.getString(R.string.dia_mostrarAyuda1)
                else -> context.getString(R.string.dia_mostrarAyudaElse)
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(context.getString(R.string.varias_rangofecha))
            builder.setMessage(
                "${context.getString(R.string.dia_mostrarAyudaMarco)}\n$texto"
            )
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun loadFullList() {

        val diaAdapter = DiaListAdapter(this)
        diaList!!.adapter = diaAdapter

        diaViewModel.getDias(args.anio) {
            val sortedList = it.sortedBy { dia -> dia.orden }
            diaAdapter.setDia(sortedList)
        }
    }

    override fun loadListWithDate(date: String) {

        val diaAdapter = DiaListAdapter(this)
        diaList!!.adapter = diaAdapter

        diaViewModel.getDias(args.anio) {
            val filteredList = remove(it, date)
            val sortedList = filteredList.sortedBy { dia -> dia.orden }
            diaAdapter.setDia(sortedList)
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

    private fun confirmarDia(currentDate: String) {
        val dia = dataDesdeIU(currentDate)
        diaViewModel.insert(dia)

        val context = requireContext()
        Toast.makeText(context, context.getString(R.string.dia_confirmar), Toast.LENGTH_LONG).show()
        loadFullList()
    }

    private fun dataDesdeIU(timestamp: String): Dia {
        val celularId = GestorUUID.obtenerAndroidID()
        val uuid = DevFragment.UUID_NULO
        return Dia(celularId, uuid, 0, fecha = timestamp)
    }
}