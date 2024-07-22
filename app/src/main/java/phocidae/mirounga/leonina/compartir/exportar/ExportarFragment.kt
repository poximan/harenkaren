package phocidae.mirounga.leonina.compartir.exportar

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.databinding.FragmentExportarBinding
import phocidae.mirounga.leonina.fragment.messaging.EmailSender
import phocidae.mirounga.leonina.model.EntidadesPlanas

class ExportarFragment : Fragment() {

    private var _binding: FragmentExportarBinding? = null
    private val binding get() = _binding!!

    private lateinit var comWF: ExportarWF

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportarBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarMedioExterno() }
        binding.medioBtn.setOnClickListener { enviarConcentrador() }
        binding.radioWifi.setOnClickListener { clickWF() }

        comWF = ExportarWF(requireContext())

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        apagarServNSD()
    }

    private fun apagarServNSD() {
        try {
            comWF.desconectar()
        } catch (e: UninitializedPropertyAccessException) {
        }
    }

    private fun clickWF() {
        val listaParcel = prepararDatos()
        comWF.descubrir(listaParcel)
    }

    private fun prepararDatos(): ArrayList<Parcelable> {
        var listaEntidadesPlanas = runBlocking { getEntidades() }
        return parcelarLista(listaEntidadesPlanas)
    }

    private fun enviarMedioExterno() {

        var listaEntidadesPlanas = runBlocking { getEntidades() }
        var datosEMAIL = CreadorCSV().empaquetarCSV(requireContext(), listaEntidadesPlanas)
        val cuerpo =
            "${requireContext().getString(R.string.exp_enviarExterno)} ${listaEntidadesPlanas.size}"

        EmailSender.sendEmail(cuerpo, datosEMAIL, requireContext())
    }

    private fun enviarConcentrador() {
        if (binding.radioWifi.isChecked)
            comWF.levantarModal()
    }

    private suspend fun getEntidades(): List<EntidadesPlanas> {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        return withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
            val dao =
                HarenKarenRoomDatabase.getDatabase(requireActivity().application, viewModelScope)
                    .unSocDao()
            return@withContext dao.getUnSocDesnormalizado()
        }
    }

    private fun parcelarLista(entidades: List<EntidadesPlanas>): ArrayList<Parcelable> {
        val listaParcelable = ArrayList<Parcelable>()
        for (entidad in entidades) {
            val parcel = Parcel.obtain()
            entidad.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            listaParcelable.add(EntidadesPlanas.createFromParcel(parcel))
            parcel.recycle()
        }
        return listaParcelable
    }
}