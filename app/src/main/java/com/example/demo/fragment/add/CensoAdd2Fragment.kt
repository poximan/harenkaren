package com.example.demo.fragment.add

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentCensoAdd2Binding
import com.example.demo.model.Censo
import com.example.demo.viewModel.CensoViewModel

class CensoAdd2Fragment : Fragment() {

    private var _binding: FragmentCensoAdd2Binding? = null
    private val binding get() = _binding!!

    private lateinit var model: CensoViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCensoAdd2Binding.inflate(inflater, container, false)
        val view = binding.root

        model = ViewModelProvider(this)[CensoViewModel::class.java]

        // punto de observacion
        val ptObsCenso = resources.getStringArray(R.array.op_punto_obs_censo)
        val ptObsCensoArrayAdapter = ArrayAdapter(view.context, R.layout.dropdown_item, ptObsCenso)

        binding.spinnerAddPtoObs.adapter = ptObsCensoArrayAdapter
        binding.helpPtoObsCenso.setOnClickListener { ptoObsCensoInfo() }

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

        binding.sendReportActionButton.setOnClickListener { enviarCenso() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctxSocialSpinner = view.findViewById<Spinner>(R.id.spinnerAddCtxSocial)
        val ctxSocial = resources.getStringArray(R.array.op_contexto_social)

        val linearLayout4 = view.findViewById<LinearLayout>(R.id.linearLayout4)
        val linearLayout5 = view.findViewById<LinearLayout>(R.id.linearLayout5)

        ctxSocialSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (ctxSocialSpinner.selectedItem == ctxSocial[0] || ctxSocialSpinner.selectedItem == ctxSocial[1]) {
                    linearLayout4.visibility = View.VISIBLE
                    linearLayout5.visibility = View.VISIBLE
                } else {
                    linearLayout4.visibility = View.GONE
                    linearLayout5.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita hacer nada aquí.
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun enviarCenso() {

        val censo = armarCenso()
        model.insertCenso(censo)
        Toast.makeText(activity, "Reporte agregado correctamente", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.my_censos_fragment)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun armarCenso(): Censo {

        val ptoObsCenso = binding.spinnerAddPtoObs.selectedItem.toString()
        val ctxSocial = binding.spinnerAddCtxSocial.selectedItem.toString()
        val tpoSustrato = binding.spinnerAddTpoSustrato.selectedItem.toString()
        // ----- dominante ----- //
        val alfaS4Ad = binding.editTextMachoAdS4.text.toString().toInt()
        val alfaOtrosSA = binding.editTextMachoAdS4.text.toString().toInt()
        // ----- hembras y crias ----- //
        val hembrasAd = binding.editTextHembrasAd.text.toString().toInt()
        val criasVivas = binding.editTextCriasVivas.text.toString().toInt()
        val criasMuertas = binding.editTextCriasMuertas.text.toString().toInt()
        val destetados = binding.editTextDestetados.text.toString().toInt()
        val juveniles = binding.editTextJuveniles.text.toString().toInt()
        // ----- Ad/SA proximos ----- //
        val s4AdPerif = binding.editTextS4AdPerif.text.toString().toInt()
        val s4AdCerca = binding.editTextS4AdCerca.text.toString().toInt()
        val s4AdLejos = binding.editTextS4AdLejos.text.toString().toInt()
        val otrosSAPerif = binding.editTextOtroSAPerif.text.toString().toInt()
        val otrosSACerca = binding.editTextOtroSACerca.text.toString().toInt()
        val otrosSALejos = binding.editTextOtroSALejos.text.toString().toInt()

        return Censo(
                0,
                ptoObsCenso, ctxSocial, tpoSustrato,
                alfaS4Ad, alfaOtrosSA, hembrasAd, criasVivas,
                criasMuertas, destetados, juveniles, s4AdPerif,
                s4AdCerca, s4AdLejos, otrosSAPerif, otrosSACerca, otrosSALejos,
                "ver fecha", 1.0, 2.0, "ver path foto"
            )
    }

    private fun ptoObsCensoInfo() {
        findNavController().navigate(R.id.ptoObsCensoAction)
    }

    private fun ctxSocialInfo() {
        findNavController().navigate(R.id.ctxSocialAction)
    }

    private fun tpoSustratoInfo() {
        findNavController().navigate(R.id.tpoSustratoAction)
    }
}