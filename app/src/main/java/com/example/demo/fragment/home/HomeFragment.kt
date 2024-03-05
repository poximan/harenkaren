package com.example.demo.fragment.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentHomeBinding
import kotlin.system.exitProcess

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        _binding!!.misCircuitosButton.setOnClickListener { showMyReports() }
        _binding!!.myTopicsButton.setOnClickListener { goToMyTopics() }
        _binding!!.regulationsButton.setOnClickListener { showRegulations() }
        _binding!!.goToStatisticsButton.setOnClickListener { goToStatistics() }
        _binding!!.logOutButton.setOnClickListener { logOut() }

        val view = binding.root
        return view
    }

    private fun showMyReports() {
        findNavController().navigate(R.id.goToMyCircuitosFromHomeAction)
    }

    private fun goToMyTopics() {
        findNavController().navigate(R.id.goToTopicsFragmentFromHomeAction)
    }

    private fun goToStatistics() {
        findNavController().navigate(R.id.goToStatisticsFragmentFromHomeAction)
    }

    private fun newReport() {
        findNavController().navigate(R.id.goToNewCensoAction)
    }

    private fun showRegulations() {
        findNavController().navigate(R.id.goToRegulationsAction)
    }

    private fun logOut() {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Salir")
        builder.setMessage("¿confirma salir?")
        builder.setPositiveButton("Si", DialogInterface.OnClickListener { _, _ ->
            exitProcess(0)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
        var alert: AlertDialog = builder.create()
        alert.show()
    }

}