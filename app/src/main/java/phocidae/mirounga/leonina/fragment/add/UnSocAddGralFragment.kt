package phocidae.mirounga.leonina.fragment.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentUnsocGralBinding
import phocidae.mirounga.leonina.model.LatLong
import phocidae.mirounga.leonina.viewModel.UnSocShareViewModel

class UnSocAddGralFragment : AddGralAbstract() {

    private var _binding: FragmentUnsocGralBinding? = null
    val binding get() = _binding!!

    private val sharedViewModel: UnSocShareViewModel by activityViewModels()
    private var latLon = LatLong()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUnsocGralBinding.inflate(inflater, container, false)
        val view = binding.root

        // punto de observacion
        val ptObsUnSoc = resources.getStringArray(R.array.op_punto_obs_unsoc)
        val ptObsUnSocArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsUnSoc)
        binding.spinnerAddPtoObs.adapter = ptObsUnSocArrayAdapter

        binding.helpPtoObsUnSoc.setOnClickListener { ptoObsUnidadSocialInfo() }

        // contexto social
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)
        val ctxSocialArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ctxSocial)
        binding.spinnerAddCtxSocial.adapter = ctxSocialArrayAdapter

        binding.helpCtxSocial.setOnClickListener { ctxSocialInfo() }

        // tipo de sustrato en playa
        val tpoSustrato = resources.getStringArray(R.array.op_tipo_sustrato)
        val tpoSustratoArrayAdapter =
            ArrayAdapter(view.context, R.layout.dropdown_item, tpoSustrato)
        binding.spinnerAddTpoSustrato.adapter = tpoSustratoArrayAdapter

        binding.helpTpoSustrato.setOnClickListener { tpoSustratoInfo() }

        binding.getPosicion.setOnClickListener { getPosicionActual(binding.latitud) }

        binding.spinnerAddPtoObs.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddCtxSocial.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddTpoSustrato.onItemSelectedListener = onItemSelectedListener
        binding.unSocComentario.addTextChangedListener(textWatcher)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicatorLight = view.findViewById(R.id.gpsLightUnSoc)
    }

    override fun onResume() {
        super.onResume()

        try {
            latLon.lat = arguments?.getDouble("lat")!!
            latLon.lon = arguments?.getDouble("lon")!!
        } catch (e: NullPointerException) {
        }
        cargarMap()
        mostrarEnPantalla()
    }

    override fun onPause() {
        super.onPause()

        val bundle = Bundle().apply {
            latLon.lat.let { putDouble("lat", it) }
            latLon.lon.let { putDouble("lon", it) }
        }
        arguments = bundle
        cargarMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun cargarMap() {

        map["pto_observacion"] = binding.spinnerAddPtoObs.selectedItem.toString()
        map["ctx_social"] = binding.spinnerAddCtxSocial.selectedItem.toString()
        map["tpo_sustrato"] = binding.spinnerAddTpoSustrato.selectedItem.toString()
        map["latitud"] = latLon.lat
        map["longitud"] = latLon.lon
        map["comentario"] = binding.unSocComentario.text.toString()

        colectar(0, map)
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (parent?.id) {
                R.id.spinnerAddCtxSocial -> {
                    val selectedValue =
                        binding.spinnerAddCtxSocial.getItemAtPosition(position).toString()
                    sharedViewModel.setLastSelectedValue(selectedValue)
                }
            }
            cargarMap()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            cargarMap()
        }
    }

    override fun updateLocationViews(latitud: Double, longitud: Double) {
        latLon.lat = latitud
        latLon.lon = longitud

        mostrarEnPantalla()
        cargarMap()
    }

    private fun mostrarEnPantalla() {
        val lat = String.format("%.6f", latLon.lat)
        val lon = String.format("%.6f", latLon.lon)

        binding.latitud.text = lat
        binding.longitud.text = lon
    }

    private fun ptoObsUnidadSocialInfo() {
        findNavController().navigate(R.id.ptoObsUnSoc_activity)
    }

    private fun ctxSocialInfo() {
        findNavController().navigate(R.id.ctxSocial_activity)
    }

    private fun tpoSustratoInfo() {
        findNavController().navigate(R.id.tpoSustrato_activity)
    }
}
