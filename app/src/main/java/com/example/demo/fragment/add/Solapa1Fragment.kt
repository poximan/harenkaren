package com.example.demo.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentSolapa1Binding

class Solapa1Fragment : Fragment() {

    private lateinit var binding: FragmentSolapa1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolapa1Binding.inflate(inflater, container, false)
        // Aquí configura la vista de tu primer fragmento
        return binding.root
    }
}
