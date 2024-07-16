package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.example.demo.adapter.ExpandListAdapter
import com.example.demo.databinding.FragmentAyudaBinding
import com.example.demo.R

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

    private fun initData(): LinkedHashMap<String, List<Pair<String, Int?>>> {
        val expandableListData = LinkedHashMap<String, List<Pair<String, Int?>>>()

        val firstItem = listOf(
            Pair(getString(R.string.ayu_detCenso1, getString(R.string.home_censos)), null),
            Pair(getString(R.string.ayu_detCenso2, getString(R.string.home_censos)), R.drawable.censos_home),
            Pair(getString(R.string.ayu_detCenso3), R.drawable.censos_anio),
            Pair(getString(R.string.ayu_detCenso4), R.drawable.censos_aniolista)
        )
        val secondItem = listOf(
            Pair("Detalle A", null),
            Pair("Detalle B", null),
            Pair("Detalle C", null)
        )
        val thirdItem = listOf(
            Pair("Detalle X", null),
            Pair("Detalle Y", null),
            Pair("Detalle Z", null)
        )

        expandableListData[getString(R.string.home_censos)] = firstItem
        expandableListData[getString(R.string.home_mapa)] = secondItem
        expandableListData[getString(R.string.ayu_importexp)] = thirdItem

        return expandableListData
    }
}