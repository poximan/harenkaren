package com.example.demo.fragment.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.R
import com.example.demo.databinding.FragmentPtoObsCensoDescripBinding

class PtoObsHelpDescripFragment : Fragment() {
    private lateinit var arrDesc: Array<String>
    private var ptoObsCensoIndex = 0

    private var _binding: FragmentPtoObsCensoDescripBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPtoObsCensoDescripBinding.inflate(inflater, container, false)
        val view = binding.root

        arrDesc = resources.getStringArray(R.array.descriptionPtoObsCenso)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun change(index: Int) {
        this.ptoObsCensoIndex = index
        binding.descriptionPtoObsTextView.text = arrDesc[ptoObsCensoIndex]
    }
}