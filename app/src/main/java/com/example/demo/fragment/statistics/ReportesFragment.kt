package com.example.demo.fragment.statistics

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentReportesBinding

class ReportesFragment : Fragment() {

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imprimirPdf.setOnClickListener {
            printPdf()
        }
    }

    private fun printPdf() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfPrintDocumentAdapter(requireContext(), binding.scrollReporte)
        val jobName = "${requireContext().applicationInfo.loadLabel(requireContext().packageManager)} Document"

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}