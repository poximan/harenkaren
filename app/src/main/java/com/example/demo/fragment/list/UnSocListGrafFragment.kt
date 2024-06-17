package com.example.demo.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.demo.R
import com.example.demo.adapter.UnSocListGrafAdapter
import com.example.demo.databinding.FragmentUnsocListGrafBinding
import com.example.demo.viewModel.UnSocViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class UnSocListGrafFragment : Fragment() {

    private val unSocViewModel: UnSocViewModel by navGraphViewModels(R.id.navHome)
    private val args: UnSocListFragmentArgs by navArgs()

    private var _binding: FragmentUnsocListGrafBinding? = null
    private val binding get() = _binding!!

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnsocListGrafBinding.inflate(inflater, container, false)
        webView = binding.webView

        binding.homeActionButton.setOnClickListener { goHome() }
        binding.newUnsocButton.setOnClickListener { nuevaUnidadSocial() }
        binding.cambiarActionButton.setOnClickListener { cambiarVista() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true

        val unSocAdapter = UnSocListGrafAdapter(args.idRecorrido)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val dynamicHtml = unSocAdapter.contenidoHTML(unSocViewModel)
                contenedorHTML(dynamicHtml)
            } catch (e: NoSuchElementException) {
                val context = requireContext()
                Toast.makeText(
                    context,
                    context.getString(R.string.soc_cambiarVista),
                    Toast.LENGTH_SHORT
                ).show()
                cambiarVista()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun contenedorHTML(dynamicHtml: String) {

        val fileName = "index.html"
        val file = File(requireContext().filesDir, fileName)
        file.createNewFile()

        val fileOutputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)

        outputStreamWriter.write(dynamicHtml)
        outputStreamWriter.close()

        // Cargar el archivo HTML en el WebView
        webView.loadUrl("file:///${file.absolutePath}")
    }

    private fun goHome() {
        findNavController().navigate(R.id.home_fragment)
    }

    private fun nuevaUnidadSocial() {
        var action = UnSocListFragmentDirections.goToNewUnSocFromUnSocListAction(args.idRecorrido)
        findNavController().navigate(action)
    }

    private fun cambiarVista() {
        var action = UnSocListGrafFragmentDirections.goToModoTexto(args.idRecorrido)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
    }
}