package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.R
import com.example.demo.databinding.FragmentCtxSocialBinding

class CtxSocialHelpFragment : Fragment() {
    private var _binding: FragmentCtxSocialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCtxSocialBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.ctxHarenRadioButton.setOnClickListener {
            click_en_radio_button(R.id.ctxHarenRadioButton)
        }
        binding.ctxGpoHarenRadioButton.setOnClickListener {
            click_en_radio_button(R.id.ctxGpoHarenRadioButton)
        }
        binding.ctxPjaSolitariaRadioButton.setOnClickListener {
            click_en_radio_button(R.id.ctxPjaSolitariaRadioButton)
        }
        binding.ctxIndivSoloRadioButton.setOnClickListener {
            click_en_radio_button(R.id.ctxIndivSoloRadioButton)
        }

        return view
    }

    private fun click_en_radio_button(id_radio_button: Int) {
        val index = when (id_radio_button) {
            R.id.ctxHarenRadioButton -> 0
            R.id.ctxGpoHarenRadioButton -> 1
            R.id.ctxPjaSolitariaRadioButton -> 2
            R.id.ctxIndivSoloRadioButton -> 3
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