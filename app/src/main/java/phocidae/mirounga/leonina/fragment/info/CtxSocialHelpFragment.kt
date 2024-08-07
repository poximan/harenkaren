package phocidae.mirounga.leonina.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentCtxSocialBinding

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
        binding.ctxHarenSinRadioButton.setOnClickListener {
            click_en_radio_button(R.id.ctxHarenSinRadioButton)
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
            R.id.ctxHarenSinRadioButton -> 1
            R.id.ctxGpoHarenRadioButton -> 2
            R.id.ctxPjaSolitariaRadioButton -> 3
            R.id.ctxIndivSoloRadioButton -> 4
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