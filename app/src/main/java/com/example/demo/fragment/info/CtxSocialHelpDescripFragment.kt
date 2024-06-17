package com.example.demo.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.R
import com.example.demo.databinding.FragmentCtxSocialDescripBinding

class CtxSocialHelpDescripFragment : Fragment() {

    private val arr: Array<String> = resources.getStringArray(R.array.descriptionCtxSocial)
    private var _binding: FragmentCtxSocialDescripBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCtxSocialDescripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun change(index: Int) {
        binding.descriptionCtxSocialTextView.text = arr[index]
    }
}