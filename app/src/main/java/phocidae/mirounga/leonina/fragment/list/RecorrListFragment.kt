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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.adapter.RecorrListAdapter
import phocidae.mirounga.leonina.databinding.FragmentRecorrListBinding
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.viewModel.RecorrViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecorrListFragment : SuperList(), RecorrListAdapter.OnRecorrClickListener {

    private val recorrViewModel: RecorrViewModel by navGraphViewModels(R.id.navHome)
    private val args: RecorrListFragmentArgs by navArgs()

    private var _binding: FragmentRecorrListBinding? = null
    private val binding get() = _binding!!
    private var recorrList: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentRecorrListBinding.inflate(inflater, container, false)

        binding.homeActionButton.setOnClickListener { goHome() }
        recorrList = binding.listRecorr
        loadFullList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recorrViewModel.getFechaObservada(args.idDia) { fecha ->
            val diaHoy = getCurrentDate()
            val rotateAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_animation)

            if (fecha == diaHoy) {
                binding.nvoRecorrButton.setOnClickListener { nvoRecorrido() }
                binding.nvoRecorrButton.clearAnimation()
            } else {
                binding.nvoRecorrButton.setOnClickListener { noMasRecorrido(diaHoy) }
                binding.nvoRecorrButton.startAnimation(rotateAnimation)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recorrList!!.adapter = null
        recorrList = null
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
        val action = RecorrListFragmentDirections.goToNewRecorrAction(args.idDia)
        findNavController().navigate(action)
    }

    private fun noMasRecorrido(diaHoy: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.rec_noRecorrTit))
        builder.setMessage(getString(R.string.rec_noRecorrMsg1) + " (" + diaHoy + ") " + getString(R.string.rec_noRecorrMsg2))
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

    private fun loadFullList() {

        val recorrAdapter = RecorrListAdapter(this)
        recorrList!!.adapter = recorrAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val recorrListAsync = recorrViewModel.readConFK(args.idDia)
            withContext(Dispatchers.Main) {
                recorrAdapter.setRecorrido(recorrListAsync)
            }
        }
    }

    override fun loadListWithDate(date: String) {

        val recorrAdapter = RecorrListAdapter(this)
        recorrList!!.adapter = recorrAdapter

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
            val soloFecha = elem.fechaIni.split(" ")[0]
            if (soloFecha == target) {
                result.add(elem)
            }
        }
        return result
    }
}