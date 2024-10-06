package phocidae.mirounga.leonina.fragment.reporte

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentStatisticsBinding
import phocidae.mirounga.leonina.fragment.DevFragment
import phocidae.mirounga.leonina.model.Dia
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.viewModel.UnSocViewModel
import java.util.UUID

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsFragmentArgs by navArgs()

    private lateinit var webViewTorta: WebView

    private var uuid: UUID = DevFragment.UUID_NULO
    private var unidSocial: UnidSocial? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        webViewTorta = binding.webViewTorta
        webViewTorta.settings.javaScriptEnabled = true

        binding.goBackButton.setOnClickListener { goBack() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tomarDatos()
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
        val torta = ReporteTorta(webViewTorta)
        torta.mostrarMapaCalor(unidSocial!!)
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
