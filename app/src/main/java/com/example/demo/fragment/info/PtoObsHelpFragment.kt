package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.Coordinadora
import com.example.demo.R
import com.example.demo.databinding.FragmentPtoObsUnsocBinding

class PtoObsHelpFragment : Fragment() {
    private var _binding: FragmentPtoObsUnsocBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPtoObsUnsocBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.unsocAltoRadioButton.setOnClickListener {
            click_en_radio_button(R.id.unsocAltoRadioButton)
        }
        binding.unsocBajoRadioButton.setOnClickListener {
            click_en_radio_button(R.id.unsocBajoRadioButton)
        }

        return view
    }

    private fun click_en_radio_button(id_radio_button: Int) {
        val index = when (id_radio_button) {
            R.id.unsocAltoRadioButton -> 0
            R.id.unsocBajoRadioButton -> 1
            else -> 0
        }
        val activity = activity
        if (activity is Coordinadora) {
            activity.onChangeOpciones(index)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}