package com.example.demo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.adapter.UnSocListAdapter
import com.example.demo.viewModel.UnSocViewModel
import com.example.demo.model.UnidSocial

import com.example.demo.databinding.FragmentInicioBinding

class InicioFragment : Fragment(), UnSocListAdapter.OnUnSocClickListener {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.app_navigation)
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.loginButton.setOnClickListener { loginApp() }
        _binding!!.registerTextView.setOnClickListener { registerApp() }

        val unSocListAdapter = UnSocListAdapter(this)
        unSocViewModel.allUnSoc
            .observe(
                viewLifecycleOwner
            ) { reports ->
                reports?.let { unSocListAdapter.setUnSoc(it) }
            }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loginApp() {
        findNavController().navigate(R.id.loginAction)
    }

    private fun registerApp() {
        findNavController().navigate(R.id.registerAction)
    }

    override fun onItemClick(unidSocial: UnidSocial) {

    }

}