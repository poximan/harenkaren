package phocidae.mirounga.leonina.fragment.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import phocidae.mirounga.leonina.databinding.FragmentUnsocMuertosBinding

class UnSocEditMuertosFragment : SuperEdit() {

    private var _binding: FragmentUnsocMuertosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocMuertosBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun cargarDatos() {

        binding.mAlfaS4Ad.setText(unSocEditable.mAlfaS4Ad.toString())
        binding.mAlfaSams.setText(unSocEditable.mAlfaSams.toString())

        binding.mHembrasAd.setText(unSocEditable.mHembrasAd.toString())
        binding.mCrias.setText(unSocEditable.mCrias.toString())
        binding.mDestetados.setText(unSocEditable.mDestetados.toString())
        binding.mJuveniles.setText(unSocEditable.mJuveniles.toString())

        binding.mS4AdPerif.setText(unSocEditable.mS4AdPerif.toString())
        binding.mS4AdCerca.setText(unSocEditable.mS4AdCerca.toString())
        binding.mS4AdLejos.setText(unSocEditable.mS4AdLejos.toString())
        binding.mOtrosSamsPerif.setText(unSocEditable.mOtrosSamsPerif.toString())
        binding.mOtrosSamsCerca.setText(unSocEditable.mOtrosSamsCerca.toString())
        binding.mOtrosSamsLejos.setText(unSocEditable.mOtrosSamsLejos.toString())

        binding.mAlfaS4Ad.addTextChangedListener(textWatcher)
        binding.mAlfaSams.addTextChangedListener(textWatcher)
        binding.mHembrasAd.addTextChangedListener(textWatcher)
        binding.mCrias.addTextChangedListener(textWatcher)
        binding.mDestetados.addTextChangedListener(textWatcher)
        binding.mJuveniles.addTextChangedListener(textWatcher)
        binding.mS4AdPerif.addTextChangedListener(textWatcher)
        binding.mS4AdCerca.addTextChangedListener(textWatcher)
        binding.mS4AdLejos.addTextChangedListener(textWatcher)
        binding.mOtrosSamsPerif.addTextChangedListener(textWatcher)
        binding.mOtrosSamsCerca.addTextChangedListener(textWatcher)
        binding.mOtrosSamsLejos.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
        cargarMap()
    }

    override fun onPause() {
        super.onPause()
        cargarMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarMap() {

        // ----- dominante ----- //
        map["m_alfa_s4ad"] = safeStringToInt(binding.mAlfaS4Ad.text.toString())
        map["m_alfa_sams"] = safeStringToInt(binding.mAlfaSams.text.toString())

        // ----- hembras y crias ----- //
        map["m_hembras_ad"] = safeStringToInt(binding.mHembrasAd.text.toString())
        map["m_crias"] = safeStringToInt(binding.mCrias.text.toString())
        map["m_destetados"] = safeStringToInt(binding.mDestetados.text.toString())
        map["m_juveniles"] = safeStringToInt(binding.mJuveniles.text.toString())

        // ----- Ad/SA proximos ----- //
        map["m_s4ad_perif"] = safeStringToInt(binding.mS4AdPerif.text.toString())
        map["m_s4ad_cerca"] = safeStringToInt(binding.mS4AdCerca.text.toString())
        map["m_s4ad_lejos"] = safeStringToInt(binding.mS4AdLejos.text.toString())
        map["m_otros_sams_perif"] = safeStringToInt(binding.mOtrosSamsPerif.text.toString())
        map["m_otros_sams_cerca"] = safeStringToInt(binding.mOtrosSamsCerca.text.toString())
        map["m_otros_sams_lejos"] = safeStringToInt(binding.mOtrosSamsLejos.text.toString())

        unSocEditable.apply {
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
        }

        colectar(2, map)
    }

    private fun safeStringToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            cargarMap()
        }
    }
}
