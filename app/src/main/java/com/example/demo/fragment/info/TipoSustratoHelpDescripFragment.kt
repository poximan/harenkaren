package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.R
import com.example.demo.databinding.FragmentTpoSustratoDescripBinding

class TipoSustratoHelpDescripFragment : Fragment() {

    private lateinit var arr: Array<String>

    private var _binding: FragmentTpoSustratoDescripBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTpoSustratoDescripBinding.inflate(inflater, container, false)
        val view = binding.root

        arr = resources.getStringArray(R.array.descriptionTpoSustrato)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun change(index: Int) {
        binding.descriptionTpoSustratoTextView.text = arr[index]
    }
}