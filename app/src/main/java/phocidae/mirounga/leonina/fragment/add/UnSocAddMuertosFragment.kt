package phocidae.mirounga.leonina.fragment.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import phocidae.mirounga.leonina.databinding.FragmentUnsocMuertosBinding

class UnSocAddMuertosFragment : SuperAdd() {

    private var _binding: FragmentUnsocMuertosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocMuertosBinding.inflate(inflater, container, false)

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
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

        colectar(2, map)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            cargarMap()
        }
    }

    private fun safeStringToInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}
