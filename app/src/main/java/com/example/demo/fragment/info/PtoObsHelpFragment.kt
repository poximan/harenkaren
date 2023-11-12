package com.example.demo.fragment.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.Cordinadora
import com.example.demo.R
import com.example.demo.databinding.FragmentPtoObsCensoBinding

class PtoObsHelpFragment : Fragment() {
    private var _binding: FragmentPtoObsCensoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPtoObsCensoBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.censoAltoRadioButton.setOnClickListener {
            click_en_radio_button(R.id.censoAltoRadioButton)
        }
        binding.censoBajoRadioButton.setOnClickListener {
            click_en_radio_button(R.id.censoBajoRadioButton)
        }

        return view
    }

    private fun click_en_radio_button(id_radio_button: Int) {
        val index = when (id_radio_button) {
            R.id.censoAltoRadioButton -> 0
            R.id.censoBajoRadioButton -> 1
            else -> 0
        }
        val activity = activity
        if (activity is Cordinadora) {
            activity.onChangeOpciones(index)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}