package com.example.demo.fragment.add;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.navArgs
import com.example.demo.adapter.UnSocPagerAdapter
import com.example.demo.databinding.FragmentSolapaBinding
import com.example.demo.model.UnidSocial

class SolapaFragment: Fragment() {

    private lateinit var binding: FragmentSolapaBinding
    private val args: SolapaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolapaBinding.inflate(inflater, container, false)

        val unSoc = UnidSocial(args.idRecorrido, args.estampatiempo)
        // Configura ViewPager y TabLayout
        val adapter = UnSocPagerAdapter(childFragmentManager, unSoc)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }
}
