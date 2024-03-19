package com.example.demo.fragment.messaging

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.databinding.FragmentExportBinding


class ExportFragment : Fragment() {
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.editTextText.text = Editable.Factory.getInstance().newEditable("viene una pruba de exportacion")
        return view
    }
}