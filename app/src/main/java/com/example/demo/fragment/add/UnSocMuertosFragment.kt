package com.example.demo.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentUnsocMuertosBinding
import com.example.demo.model.UnidSocial

class UnSocMuertosFragment(unidSocial: UnidSocial) : Fragment() {

    private lateinit var binding: FragmentUnsocMuertosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUnsocMuertosBinding.inflate(inflater, container, false)
        // Aquí configura la vista de tu primer fragmento
        return binding.root
    }
}
