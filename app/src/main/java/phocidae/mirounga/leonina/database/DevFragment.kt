package phocidae.mirounga.leonina.database

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentDevBinding
import phocidae.mirounga.leonina.repository.RecorrRepository
import phocidae.mirounga.leonina.repository.UnSocRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

class DevFragment : Fragment() {

    private var _binding: FragmentDevBinding? = null
    private val binding get() = _binding!!

    companion object {
        val TAG = "HAREN_DATABASE"
        var UUID_NULO: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDevBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.poblarBd.setOnClickListener { poblar() }
        binding.usuario.setOnClickListener { altaUsuario() }

        binding.reventarEsquema.setOnClickListener { borrarEsquema() }

        binding.limpiarDias.setOnClickListener { limpiarDias() }
        binding.limpiarRecorr.setOnClickListener { limpiarRecorr() }
        binding.limpiarLog.setOnClickListener { binding.logcat.text = "" }

        estadoBD()
        logcat()
        setupScrollListener()

        return binding.root
    }

    private fun setupScrollListener() {
        val scrollView = binding.logcatScrollView
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.scrollY == 0) {
                logcat()
            }
        }
    }

    private fun estadoBD() {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        // Define el manejador de excepciones
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            if (exception is IllegalStateException) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.dev_E_estadoDB),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background

                val bd = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)

                val dias = bd.diaDao().getCount()
                val recorr = bd.recorrDao().getCount()
                val unsoc = bd.unSocDao().getCount()
                withContext(Dispatchers.Main) {
                    binding.dias.text =
                        "$dias ${requireContext().getString(R.string.dev_estadoDB_entidad)} ${
                            requireContext().getString(R.string.dev_estadoDB_dia)
                        }"
                    binding.recorr.text =
                        "$recorr ${requireContext().getString(R.string.dev_estadoDB_entidad)} ${
                            requireContext().getString(R.string.dev_estadoDB_recorr)
                        }"
                    binding.unsoc.text =
                        "$unsoc  ${requireContext().getString(R.string.dev_estadoDB_entidad)} ${
                            requireContext().getString(R.string.dev_estadoDB_unsoc)
                        }"
                }
            }
        }
    }

    private fun logcat() {
        val process = Runtime.getRuntime().exec("logcat -d -t 50")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        val log = SpannableStringBuilder()
        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            val parts = line?.split("\\s+".toRegex())
            if (parts != null && parts.size >= 5 && (parts[4] == "E" || parts[4] == "W")) {
                val spannable = SpannableString(line)
                if (parts[4] == "E")
                    spannable.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        line!!.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                if (parts[4] == "W")
                    spannable.setSpan(
                        ForegroundColorSpan(Color.MAGENTA),
                        0,
                        line!!.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                log.append(spannable).append("\n")
            } else {
                log.append(line).append("\n")
            }
        }
        binding.logcat.text = log
    }

    private fun altaUsuario() {
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos(requireContext())

                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .usuarioDao()

                datos.generarUsuario(dao)
            }
        }
    }

    private fun poblar() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos(requireContext())

                // Obtener los DAOs
                val diaDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .diaDao()

                val recorrDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .recorrDao()
                val recorrRepo = RecorrRepository(recorrDao)

                val unSocDao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .unSocDao()
                val unSocRepo = UnSocRepository(unSocDao)

                val listDias: Array<UUID> =
                    datos.generarDias(diaDao).filterNotNull().toTypedArray()
                val listRecorr: Array<UUID> =
                    datos.generarRecorridos(recorrRepo, listDias).filterNotNull().toTypedArray()
                datos.generarUnidadesSociales(unSocRepo, listRecorr)

                withContext(Dispatchers.Main) {
                    estadoBD()
                }
            }
        }
    }

    private fun limpiarDias() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos(requireContext())
                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .diaDao()
                datos.vaciarDias(dao)

                withContext(Dispatchers.Main) {
                    estadoBD()
                }
            }
        }
    }

    private fun limpiarRecorr() {
        // Crear un CoroutineScope
        val viewModelScope = viewLifecycleOwner.lifecycleScope

        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                val datos = DevDatos(requireContext())
                val dao = HarenKarenRoomDatabase
                    .getDatabase(requireActivity().application, viewModelScope)
                    .recorrDao()
                datos.vaciarRecorridos(dao)
            }
        }
        estadoBD()
    }

    private fun borrarEsquema() {

        val viewModelScope = viewLifecycleOwner.lifecycleScope
        viewModelScope.launch {
            withContext(Dispatchers.IO) {// Dispatchers.IO es el hilo background
                HarenKarenRoomDatabase
                    .deleteDatabase(requireActivity().application, "haren_database")
                HarenKarenRoomDatabase.getDatabase(requireActivity().application, viewModelScope)
                val res = HarenKarenRoomDatabase.isDatabaseSchemaCreated(requireContext())
                Log.i(TAG, "Existe esquema BD:${res}")
            }
        }
    }
}