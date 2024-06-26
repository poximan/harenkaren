package com.example.demo.fragment.statistics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.demo.R
import com.example.demo.database.DevFragment
import com.example.demo.databinding.FragmentStatisticsBinding
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.util.UUID

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsFragmentArgs by navArgs()

    private var uuid: UUID = DevFragment.UUID_NULO
    private var unidSocial: UnidSocial? = null

    private var datoMasChico: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.goBackButton.setOnClickListener { goBack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        escuchadorEventosCheck(view)
        tomarDatos()
    }

    private fun escuchadorEventosCheck(view: View) {
        view.findViewById<CheckBox>(R.id.chk_vAlfaS4Ad).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vAlfaSams).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vHembrasAd).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vCrias).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vDestetados).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vJuveniles).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vS4AdPerif).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vS4AdCerca).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vS4AdLejos).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vOtrosSamsPerif).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vOtrosSamsCerca).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_vOtrosSamsLejos).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mAlfaS4Ad).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mAlfaSams).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mHembrasAd).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mCrias).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mDestetados).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mJuveniles).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mS4AdPerif).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mS4AdCerca).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mS4AdLejos).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mOtrosSamsPerif).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mOtrosSamsCerca).setOnClickListener { tomarDatos() }
        view.findViewById<CheckBox>(R.id.chk_mOtrosSamsLejos).setOnClickListener { tomarDatos() }
    }

    private fun tomarDatos() {

        /*
        es importante diferenciar si es la primera vez que se entra a la pantalla o ya viene estando
        e interactua. cuando el usuario cambia un filtro la pregunta se reestructura pero
        con el mismo set de datos, no hace falta volver a hacer la consulta a BD
         */
        if (unidSocial == null) {

            val viewModel = UnSocViewModel(requireActivity().application)
            when (val entidad = args.entidad) {
                is List<*> -> {
                    when (entidad.firstOrNull()) {
                        is Dia -> {
                            todosLosDias(viewModel)
                        }
                    }
                }

                is Dia -> {
                    uuid = entidad.id
                    unDia(viewModel)
                }

                is Recorrido -> {
                    uuid = entidad.id
                    unRecorrido(viewModel)
                }

                is UnidSocial -> {
                    uuid = entidad.id
                    unaUnidadSocial(viewModel)
                }

                else -> {}
            }
        } else
            graficar()
    }

    private fun todosLosDias(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            unidSocial = viewModel.readSumTotal()
            withContext(Dispatchers.Main) {
                graficar()
            }
        }
    }

    private fun unDia(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            unidSocial = viewModel.readSumDia(uuid)
            withContext(Dispatchers.Main) {
                if (unidSocial != null) {
                    graficar()
                } else {
                    showErrorDialog(
                        requireContext().getString(R.string.sta_unDia)
                    )
                }
            }
        }
    }

    private fun unRecorrido(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            unidSocial = viewModel.readSumRecorr(uuid)
            withContext(Dispatchers.Main) {
                if (unidSocial != null) {
                    graficar()
                } else {
                    showErrorDialog(
                        requireContext().getString(R.string.sta_unRecorrido)
                    )
                }
            }
        }
    }

    private fun unaUnidadSocial(viewModel: UnSocViewModel) {

        CoroutineScope(Dispatchers.IO).launch {
            unidSocial = viewModel.readUnico(uuid)
            withContext(Dispatchers.Main) {
                graficar()
            }
        }
    }

    private fun graficar() {

        val pieChart: PieChart = binding.piechart
        pieChart.clearChart()
        datoMasChico = 0.0f

        val contadores = unidSocial!!.getContadores()
        for (atribString in contadores) {
            ocultarEtiquetas(atribString)
        }

        val contadoresNoNulos = unidSocial!!.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {

            asignarValorPorReflexion(atribString)
            if (atribIsCheck(atribString)) {
                pieChart.addPieSlice(setData(atribString))
            }
        }
        pieChart.addPieSlice(PieModel("", datoMasChico, Color.TRANSPARENT))
        pieChart.startAnimation()
    }

    private fun ocultarEtiquetas(atribString: String) {

        // Genera el nombre del campo correspondiente al componente visual
        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "txt${capitalizar}${atribString.substring(1)}"

        // Obtiene el campo del binding utilizando reflexión
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        field.isAccessible = true

        if (field.type == TextView::class.java) {
            val textView = field.get(binding) as TextView
            val layoutExiste = textView.parent as LinearLayout
            layoutExiste.visibility = View.GONE
        }
    }

    private fun asignarValorPorReflexion(atribString: String) {

        // Genera el nombre del campo correspondiente al componente visual
        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "txt${capitalizar}${atribString.substring(1)}"

        // Obtiene el campo del binding utilizando reflexión
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        field.isAccessible = true

        if (field.type == TextView::class.java) {
            val textView = field.get(binding) as TextView

            // obtengo un objeto Field
            val valorAtributo = unidSocial!!.javaClass.getDeclaredField(atribString)
            valorAtributo.isAccessible = true
            // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
            val valor = valorAtributo.get(unidSocial)
            textView.text = "$atribString: $valor"

            val layoutExiste = textView.parent as LinearLayout
            layoutExiste.visibility = View.VISIBLE
        }
    }

    private fun atribIsCheck(atribString: String): Boolean {

        val capitalizar = if (atribString.startsWith('v')) 'V' else 'M'
        val nombreCampo = "chk${capitalizar}${atribString.substring(1)}"
        val field = binding.javaClass.getDeclaredField(nombreCampo)
        val checkBox = field.get(binding) as CheckBox

        return checkBox.isChecked
    }

    private fun setData(atribString: String): PieModel {

        val valorAtributo = unidSocial!!.javaClass.getDeclaredField(atribString)
        valorAtributo.isAccessible = true
        // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
        val valor = (valorAtributo.get(unidSocial) as Int).toFloat()

        if (datoMasChico > valor) datoMasChico = valor

        return PieModel(atribString, valor, siguienteColor(atribString))
    }

    private fun siguienteColor(atribString: String): Int {
        val coloresMap = mapOf(
            "vAlfaS4Ad" to R.color.clr_v_alfa_s4ad,
            "vAlfaSams" to R.color.clr_v_alfa_sams,
            "vHembrasAd" to R.color.clr_v_hembras_ad,
            "vCrias" to R.color.clr_v_crias,
            "vDestetados" to R.color.clr_v_destetados,
            "vJuveniles" to R.color.clr_v_juveniles,
            "vS4AdPerif" to R.color.clr_v_s4ad_perif,
            "vS4AdCerca" to R.color.clr_v_s4ad_cerca,
            "vS4AdLejos" to R.color.clr_v_s4ad_lejos,
            "vOtrosSamsPerif" to R.color.clr_v_otros_sams_perif,
            "vOtrosSamsCerca" to R.color.clr_v_otros_sams_cerca,
            "vOtrosSamsLejos" to R.color.clr_v_otros_sams_lejos,
            "mAlfaS4Ad" to R.color.clr_m_alfa_s4ad,
            "mAlfaSams" to R.color.clr_m_alfa_sams,
            "mHembrasAd" to R.color.clr_m_hembras_ad,
            "mCrias" to R.color.clr_m_crias,
            "mDestetados" to R.color.clr_m_destetados,
            "mJuveniles" to R.color.clr_m_juveniles,
            "mS4AdPerif" to R.color.clr_m_s4ad_perif,
            "mS4AdCerca" to R.color.clr_m_s4ad_cerca,
            "mS4AdLejos" to R.color.clr_m_s4ad_lejos,
            "mOtrosSamsPerif" to R.color.clr_m_otros_sams_perif,
            "mOtrosSamsCerca" to R.color.clr_m_otros_sams_cerca,
            "mOtrosSamsLejos" to R.color.clr_m_otros_sams_lejos
        )
        return ContextCompat.getColor(requireContext(), coloresMap[atribString]!!)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun showErrorDialog(mensaje: String) {
        val context = requireContext()

        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton(context.getString(R.string.varias_volver)) { dialog, _ ->
                dialog.dismiss()
                goBack()
            }
            .show()
    }
}
