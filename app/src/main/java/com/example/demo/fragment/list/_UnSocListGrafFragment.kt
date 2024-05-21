package com.example.demo.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.adapter.UnSocListGrafAdapter
import com.example.demo.databinding.FragmentUnsocListGrafBinding
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.charts.StackedBarChart

class _UnSocListGrafFragment : Fragment() {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
    private val args: UnSocListFragmentArgs by navArgs()

    private var _binding: FragmentUnsocListGrafBinding? = null
    private val binding get() = _binding!!

    private lateinit var stackchart: StackedBarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocListGrafBinding.inflate(inflater, container, false)
        loadFullList()
        return binding.root
    }

    private fun loadFullList() {

        val unSocAdapter = UnSocListGrafAdapter(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val unSocListAsync = unSocViewModel.readConFK(args.idRecorrido)
            val total = unSocViewModel.getMaxRegistro(args.idRecorrido)

            withContext(Dispatchers.Main) {
                unSocAdapter.setUnSoc(unSocListAsync)
            }
        }
    }
}