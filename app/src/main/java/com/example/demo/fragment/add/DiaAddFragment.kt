package com.example.demo.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demo.DevFragment
import com.example.demo.R
import com.example.demo.databinding.FragmentDiaAddBinding
import com.example.demo.model.Dia
import com.example.demo.servicios.GestorUUID
import com.example.demo.viewModel.DiaViewModel
import java.text.SimpleDateFormat
import java.util.Date

class DiaAddFragment : Fragment() {

    private var _binding: FragmentDiaAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: DiaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDiaAddBinding.inflate(inflater, container, false)
        model = ViewModelProvider(this)[DiaViewModel::class.java]

        binding.confirmarDiaButton.setOnClickListener { confirmarDia() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textDia.text =
            "Hasta el momento, esta vista no posee datos adicionales." +
                    " De seguir asi esta pantalla desaparecerá. Por el momento se deja" +
                    " preventivamente, en caso que se requieran agregar datos no previsto ahora"
    }

    private fun confirmarDia() {

        val dia = dataDesdeIU()
        model.insert(dia)

        Toast.makeText(activity, "Dia agregado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.dia_list_fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dataDesdeIU(): Dia {

        val celularId = GestorUUID.obtenerAndroidID()
        val formato = requireContext().resources.getString(R.string.formato_dia)
        val timeStamp = SimpleDateFormat(formato).format(Date())

        val uuid = DevFragment.UUID_NULO
        return Dia(celularId, uuid, 0, timeStamp)
    }
}

