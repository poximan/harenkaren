package com.example.demo.fragment.add;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment;
import com.example.demo.adapter.UnSocPagerAdapter
import com.example.demo.databinding.FragmentSolapaBinding

class SolapaFragment: Fragment() {

    private lateinit var binding: FragmentSolapaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolapaBinding.inflate(inflater, container, false)

        // Configura ViewPager y TabLayout
        val adapter = UnSocPagerAdapter(childFragmentManager)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }
}
