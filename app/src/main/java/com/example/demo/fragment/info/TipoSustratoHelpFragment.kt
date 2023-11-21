package com.example.demo.fragment.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.Cordinadora
import com.example.demo.R
import com.example.demo.databinding.FragmentCtxSocialBinding
import com.example.demo.databinding.FragmentTpoSustratoBinding

class TipoSustratoHelpFragment : Fragment() {
    private var _binding: FragmentTpoSustratoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTpoSustratoBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.tpoArenaRadioButton.setOnClickListener {
            click_en_radio_button(R.id.tpoArenaRadioButton)
        }
        binding.tpoCantoRodadoRadioButton.setOnClickListener {
            click_en_radio_button(R.id.tpoCantoRodadoRadioButton)
        }
        binding.tpoMezclaRadioButton.setOnClickListener {
            click_en_radio_button(R.id.tpoMezclaRadioButton)
        }
        binding.tpoRestingaRadioButton.setOnClickListener {
            click_en_radio_button(R.id.tpoRestingaRadioButton)
        }

        return view
    }

    private fun click_en_radio_button(id_radio_button: Int) {
        val index = when (id_radio_button) {
            R.id.tpoArenaRadioButton -> 0
            R.id.tpoCantoRodadoRadioButton -> 1
            R.id.tpoMezclaRadioButton -> 2
            R.id.tpoRestingaRadioButton -> 3
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