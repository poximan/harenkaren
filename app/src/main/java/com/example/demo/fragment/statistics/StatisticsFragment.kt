package com.example.demo.fragment.statistics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.Calendar
import java.util.Date


class StatisticsFragment : Fragment() {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

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
        _binding!!.goToMultipleMapsButton.setOnClickListener { goToMultipleMaps() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.tvR.text = Integer.toString(40)
        _binding!!.tvPython.text = Integer.toString(60)
        _binding!!.tvCPP.text = Integer.toString(20)
        _binding!!.tvJava.text = Integer.toString(8)

        pieChart = view.findViewById(R.id.piechart);
        pieChart.addPieSlice(
            PieModel(
                "R", _binding!!.tvR.text.toString().toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Python", _binding!!.tvPython.text.toString().toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "C++", _binding!!.tvCPP.text.toString().toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Java", _binding!!.tvJava.text.toString().toFloat(),
                Color.parseColor("#29B6F6")
            )
        )
        pieChart.startAnimation();
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

    private fun goToMultipleMaps() {
        findNavController().navigate(R.id.goToMultipleMapsFragmentFromStatisticsAction)
    }
    private fun goBack() {
        findNavController().popBackStack()
    }

}