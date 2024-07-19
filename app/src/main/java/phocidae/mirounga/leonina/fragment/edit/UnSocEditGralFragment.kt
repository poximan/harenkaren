package phocidae.mirounga.leonina.fragment.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentUnsocGralBinding
import phocidae.mirounga.leonina.model.LatLong

class UnSocEditGralFragment : EditGralAbstract() {

    private var _binding: FragmentUnsocGralBinding? = null
    val binding get() = _binding!!

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
        binding.icono.setOnClickListener { goGraficar() }

        binding.spinnerAddPtoObs.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddCtxSocial.onItemSelectedListener = onItemSelectedListener
        binding.spinnerAddTpoSustrato.onItemSelectedListener = onItemSelectedListener

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
        cargarDatos()
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

    private fun goGraficar() {
        val action =
            UnSocEditTodoFragmentDirections.goToGrafDesdeUnSocAction(unSocEditable)
        findNavController().navigate(action)
    }

    private fun cargarDatos() {

        val unSocEdit = unSocEditable
        var indice = obtenerPosicionSpinner(unSocEdit.ptoObsUnSoc, binding.spinnerAddPtoObs)
        binding.spinnerAddPtoObs.setSelection(indice)

        indice = obtenerPosicionSpinner(unSocEdit.ctxSocial, binding.spinnerAddCtxSocial)
        binding.spinnerAddCtxSocial.setSelection(indice)

        indice = obtenerPosicionSpinner(unSocEdit.tpoSustrato, binding.spinnerAddTpoSustrato)
        binding.spinnerAddTpoSustrato.setSelection(indice)

        binding.unSocComentario.setText(unSocEdit.comentario)
        updateLocationViews(unSocEdit.latitud, unSocEdit.longitud)
        binding.unSocComentario.addTextChangedListener(textWatcher)

        binding.linearLayout6.visibility = View.INVISIBLE
        binding.photoButton.visibility = View.INVISIBLE
    }

    override fun updateLocationViews(latitud: Double, longitud: Double) {
        latLon.lat = latitud
        latLon.lon = longitud

        unSocEditable.latitud = latitud
        unSocEditable.longitud = longitud

        mostrarEnPantalla()
        cargarMap()
    }

    private fun mostrarEnPantalla() {
        val lat = String.format("%.6f", latLon.lat)
        val lon = String.format("%.6f", latLon.lon)

        binding.latitud.text = lat
        binding.longitud.text = lon
    }

    private fun cargarMap() {

        map["pto_observacion"] = binding.spinnerAddPtoObs.selectedItem.toString()
        map["ctx_social"] = binding.spinnerAddCtxSocial.selectedItem.toString()
        map["tpo_sustrato"] = binding.spinnerAddTpoSustrato.selectedItem.toString()
        map["latitud"] = latLon.lat
        map["longitud"] = latLon.lon
        map["photo_path"] = unSocEditable.photoPath.toString()
        map["comentario"] = binding.unSocComentario.text.toString()

        unSocEditable.apply {
            ptoObsUnSoc = binding.spinnerAddPtoObs.selectedItem.toString()
            ctxSocial = binding.spinnerAddCtxSocial.selectedItem.toString()
            tpoSustrato = binding.spinnerAddTpoSustrato.selectedItem.toString()
            latitud = latLon.lat
            longitud = latLon.lon
            photoPath = unSocEditable.photoPath
            comentario = binding.unSocComentario.text.toString()
        }

        colectar(0, map)
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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