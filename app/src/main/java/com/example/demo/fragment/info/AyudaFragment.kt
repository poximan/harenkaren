package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.example.demo.adapter.ExpandListAdapter
import com.example.demo.databinding.FragmentAyudaBinding

class AyudaFragment : Fragment() {

    private var _binding: FragmentAyudaBinding? = null
    private val binding get() = _binding!!

    private lateinit var expanList: ExpandableListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAyudaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expanList = binding.expanList
        val expandableListData = initData()

        val expandListAdapter = ExpandListAdapter(requireContext(), expandableListData)
        expanList.setAdapter(expandListAdapter)
        expanList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            // Lógica cuando se hace clic en un elemento hijo
            true
        }
    }

    private fun initData(): HashMap<String, List<String>> {
        val expandableListData = HashMap<String, List<String>>()
        val firstItem = listOf("Detalle 1", "Detalle 2", "Detalle 3")
        val secondItem = listOf("Detalle A", "Detalle B", "Detalle C")
        val thirdItem = listOf("Detalle X", "Detalle Y", "Detalle Z")

        expandableListData["Censos"] = firstItem
        expandableListData["Mapas"] = secondItem
        expandableListData["Improtar/Exportar"] = thirdItem

        return expandableListData
    }
}