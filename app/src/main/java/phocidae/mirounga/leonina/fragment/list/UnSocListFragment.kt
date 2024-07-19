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
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.adapter.UnSocListAdapter
import phocidae.mirounga.leonina.databinding.FragmentUnsocListBinding
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnSocListFragment : SuperList(), UnSocListAdapter.OnUnSocClickListener {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.navHome)
    private val args: UnSocListFragmentArgs by navArgs()

    private var _binding: FragmentUnsocListBinding? = null
    private val binding get() = _binding!!
    private var unSocList: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)
        _binding = FragmentUnsocListBinding.inflate(inflater, container, false)

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.cambiarActionButton.setOnClickListener { cambiarVista() }

        unSocList = binding.listUnSoc
        val layoutManager = LinearLayoutManager(requireContext())
        unSocList!!.layoutManager = layoutManager

        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        val dividerItemDecoration =
            DividerItemDecoration(unSocList!!.context, layoutManager.orientation)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        unSocList!!.addItemDecoration(dividerItemDecoration)

        loadFullList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unSocViewModel.getFechaObservada(args.idRecorrido) { fecha ->
            val diaHoy = getCurrentDate()
            val rotateAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_animation)

            if (fecha.split(" ")[0] == diaHoy) {
                binding.nvoUnsocButton.setOnClickListener { nvaUnidadSocial() }
                binding.nvoUnsocButton.clearAnimation()
            } else {
                binding.nvoUnsocButton.setOnClickListener { noMasUnidSocial(diaHoy) }
                binding.nvoUnsocButton.startAnimation(rotateAnimation)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unSocList!!.adapter = null
        unSocList = null
    }

    override fun onItemClick(elem: UnidSocial) {
        val action = UnSocListFragmentDirections.goToUnSocDetailFromUnSocListAction(elem)
        findNavController().navigate(action)
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun noMasUnidSocial(diaHoy: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.soc_noUnSocTit))
        builder.setMessage(getString(R.string.soc_noUnSocMsg1) + " (" + diaHoy + ") " + getString(R.string.soc_noUnSocMsg2))
        builder.setPositiveButton(android.R.string.ok, null)
        builder.show()
    }

    private fun nvaUnidadSocial() {
        val action = UnSocListFragmentDirections.goToNewUnSocFromUnSocListAction(args.idRecorrido)
        findNavController().navigate(action)
    }

    private fun cambiarVista() {
        val action = UnSocListFragmentDirections.goToModoGrafico(args.idRecorrido)
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

    private fun loadFullList() {

        val unSocAdapter = UnSocListAdapter(this)
        unSocList!!.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
                withContext(Dispatchers.Main) {
                    unSocAdapter.setUnSoc(unSocListAsync)
                }
            } catch (e: ConcurrentModificationException) {
            }
        }
    }

    override fun loadListWithDate(date: String) {

        val unSocAdapter = UnSocListAdapter(this)
        unSocList!!.adapter = unSocAdapter

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
                withContext(Dispatchers.Main) {
                    val filteredList = remove(unSocListAsync, date)
                    unSocAdapter.setUnSoc(filteredList)
                }
            } catch (e: ConcurrentModificationException) {
            }
        }
    }

    private fun remove(arr: List<UnidSocial>, target: String): List<UnidSocial> {
        val result: MutableList<UnidSocial> = ArrayList()
        for (elem in arr) {
            val soloFecha = elem.date.split(" ")[0]
            if (soloFecha == target) {
                result.add(elem)
            }
        }
        return result
    }
}