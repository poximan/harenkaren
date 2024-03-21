package com.example.demo.fragment.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.Date

class StatisticsFragment : Fragment() {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var pieChart: PieChart? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val reportList = unSocViewModel.allUnSoc.value
        if (reportList != null) {
            fillLabels(reportList)
        }

        _binding!!.goBackButton.setOnClickListener { goBack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.grafText01.text = Integer.toString(40)
        _binding!!.grafText02.text = Integer.toString(60)
        _binding!!.grafText03.text = Integer.toString(20)
        _binding!!.grafText04.text = Integer.toString(8)
        _binding!!.grafText05.text = Integer.toString(17)
        _binding!!.grafText06.text = Integer.toString(35)
        _binding!!.grafText07.text = Integer.toString(3)

        pieChart = view.findViewById(R.id.piechart)

        setData(pieChart)
        pieChart?.startAnimation();
    }

    private fun setData(pieChart: PieChart?) {

        var color = ContextCompat.getColor(requireContext(), R.color.graf_color01)
        pieChart?.addPieSlice(
            PieModel(
                "graf1", _binding!!.grafText01.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color02)
        pieChart?.addPieSlice(
            PieModel(
                "graf2", _binding!!.grafText02.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color03)
        pieChart?.addPieSlice(
            PieModel(
                "graf3", _binding!!.grafText03.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color04)
        pieChart?.addPieSlice(
            PieModel(
                "graf4", _binding!!.grafText04.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color05)
        pieChart?.addPieSlice(
            PieModel(
                "graf5", _binding!!.grafText05.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color06)
        pieChart?.addPieSlice(
            PieModel(
                "graf6", _binding!!.grafText06.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
        color = ContextCompat.getColor(requireContext(), R.color.graf_color07)
        pieChart?.addPieSlice(
            PieModel(
                "graf7", _binding!!.grafText07.text.toString().toFloat(),
                Color.parseColor(String.format("#%06X", 0xFFFFFF and color))
            )
        )
    }

    private fun fillLabels(unidSocialList: List<UnidSocial>) {

    }

    private fun checkForSixMonthsReport(date: Date): Int {
        return 0
    }

    private fun createMutableMapOf(species: Array<String>): MutableMap<String, Int> {
        var aMutableMapOfSpecies = mutableMapOf<String, Int>()
        for (specie in species) {
            aMutableMapOfSpecies[specie] = 0
        }
        return aMutableMapOfSpecies
    }

    private fun getFirstDayOfReportList(unidSocialList: List<UnidSocial>): CharSequence {
        if (unidSocialList.isEmpty()) {
            return "No hay reportes cargados"
        }
        return unidSocialList.last().date!!
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

}