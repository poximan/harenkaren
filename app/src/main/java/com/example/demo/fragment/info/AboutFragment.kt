package com.example.demo.fragment.info

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.R
import com.example.demo.databinding.FragmentAboutBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.acercade.text = Html.fromHtml(getString(R.string.abo_acercade), Html.FROM_HTML_MODE_COMPACT)

        val advertencia = getString(R.string.abo_legalTit) + "\n" + getString(R.string.app_name) + " " + getString(R.string.abo_legalMsj)
        binding.usodatos.text = Html.fromHtml(advertencia, Html.FROM_HTML_MODE_COMPACT)

        binding.build.text = "Buiil: ${getCurrentDateString()}"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.acercade.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.usodatos.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.build.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun getCurrentDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        return dateFormat.format(calendar.time)
    }
}