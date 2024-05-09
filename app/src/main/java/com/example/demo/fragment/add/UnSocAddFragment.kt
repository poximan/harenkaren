package com.example.demo.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.adapter.UnSocPagerAdapter
import com.example.demo.databinding.FragmentUnsocAddBinding
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import java.text.SimpleDateFormat
import java.util.Date

class UnSocAddFragment : Fragment() {

    private lateinit var binding: FragmentUnsocAddBinding
    private val args: UnSocAddFragmentArgs by navArgs()

    private lateinit var unSoc: UnidSocial
    private lateinit var adapter: UnSocPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUnsocAddBinding.inflate(inflater, container, false)

        val formato = requireContext().resources.getString(R.string.formato_fecha)
        val estampatiempo = SimpleDateFormat(formato).format(Date())

        unSoc = UnidSocial(args.idRecorrido, estampatiempo)
        adapter = UnSocPagerAdapter(childFragmentManager)

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.confirmarUnsoc.setOnClickListener { confirmarAlta() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        elimnar el elemento ID porque no tiene sentido aca
        la entidad todavia no existe
        */
        val textView: TextView = view.findViewById(R.id.id_unsoc)
        val parentView: ViewGroup = textView.parent as ViewGroup
        parentView.removeView(textView)
    }

    private fun confirmarAlta() {

        val model: UnSocViewModel = ViewModelProvider(this)[UnSocViewModel::class.java]
        val map: MutableMap<String, Any?> = adapter.transferirDatos()

        unSoc.apply {
            ptoObsUnSoc = map["pto_observacion"] as String
            ctxSocial = map["ctx_social"] as String
            tpoSustrato = map["tpo_sustrato"] as String

            vAlfaS4Ad = map["v_alfa_s4ad"] as Int
            vAlfaSams = map["v_alfa_sams"] as Int
            vHembrasAd = map["v_hembras_ad"] as Int
            vCrias = map["v_crias"] as Int
            vDestetados = map["v_destetados"] as Int
            vJuveniles = map["v_juveniles"] as Int
            vS4AdPerif = map["v_s4ad_perif"] as Int
            vS4AdCerca = map["v_s4ad_cerca"] as Int
            vS4AdLejos = map["v_s4ad_lejos"] as Int
            vOtrosSamsPerif = map["v_otros_sams_perif"] as Int
            vOtrosSamsCerca = map["v_otros_sams_cerca"] as Int
            vOtrosSamsLejos = map["v_otros_sams_lejos"] as Int

            mAlfaS4Ad = map["m_alfa_s4ad"] as Int
            mAlfaSams = map["m_alfa_sams"] as Int
            mHembrasAd = map["m_hembras_ad"] as Int
            mCrias = map["m_crias"] as Int
            mDestetados = map["m_destetados"] as Int
            mJuveniles = map["m_juveniles"] as Int
            mS4AdPerif = map["m_s4ad_perif"] as Int
            mS4AdCerca = map["m_s4ad_cerca"] as Int
            mS4AdLejos = map["m_s4ad_lejos"] as Int
            mOtrosSamsPerif = map["m_otros_sams_perif"] as Int
            mOtrosSamsCerca = map["m_otros_sams_cerca"] as Int
            mOtrosSamsLejos = map["m_otros_sams_lejos"] as Int

            latitud = map["latitud"] as Double
            longitud = map["longitud"] as Double

            photoPath = map["photo_path"] as String
            comentario = map["comentario"] as String
        }

        model.insert(unSoc)

        Toast.makeText(activity, "Registro agregado correctamente", Toast.LENGTH_LONG).show()
        val action = UnSocAddFragmentDirections.goToUnSocListFromSolapaAction(unSoc.recorrId)
        findNavController().navigate(action)
    }
}
