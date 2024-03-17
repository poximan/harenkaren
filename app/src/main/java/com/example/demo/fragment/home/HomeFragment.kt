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

        _binding!!.censosButton.setOnClickListener { gotoCensos() }
        _binding!!.topicosButton.setOnClickListener { goToMyTopics() }
        _binding!!.regulationsButton.setOnClickListener { showRegulations() }
        _binding!!.goToStatisticsButton.setOnClickListener { goToStatistics() }
        _binding!!.logOutButton.setOnClickListener { logOut() }

        return binding.root
    }

    private fun gotoCensos() {
        findNavController().navigate(R.id.goToDiaAction)
    }

    private fun goToMyTopics() {
        findNavController().navigate(R.id.goToTopicsAction)
    }

    private fun goToStatistics() {
        findNavController().navigate(R.id.goToStatisticsAction)
    }

    private fun showRegulations() {
        findNavController().navigate(R.id.goToRegulationsAction)
    }

    private fun logOut() {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Salir")
        builder.setMessage("¿Confirma salir?")
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