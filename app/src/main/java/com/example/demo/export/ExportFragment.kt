package com.example.demo.export

import BluetoothClient
import BluetoothServer
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.demo.databinding.FragmentExportBinding

class ExportFragment : Fragment() {

    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothServer: BluetoothServer
    private lateinit var bluetoothClient: BluetoothClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { enviarEmail() }
        binding.bluetoothBtn.setOnClickListener { usarBluetooth() }

        val check = binding.soyConcent
        val bluetoothTxt = binding.bluetoothText

        check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                bluetoothTxt.text = "Recibir datos desde otros equipos"
            } else {
                bluetoothTxt.text = "Enviar mis datos a un concentrador"
            }
        }

        return binding.root
    }

    private fun usarBluetooth() {
        if (binding.soyConcent.isChecked)
            recibirDatos()
        else
            enviarConcentrador()
    }

    private fun recibirDatos() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothServer = BluetoothServer(requireActivity(), bluetoothAdapter)
        bluetoothServer.startServer()
    }

    private fun enviarConcentrador() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothClient = BluetoothClient(requireActivity(), bluetoothAdapter)

        // Obtener una referencia al dispositivo Bluetooth al que deseas conectarte
        // Por ejemplo, podrías escanear dispositivos cercanos y seleccionar uno para la conexión
        // Aquí asumiremos que tienes una referencia al dispositivo Bluetooth deseado
        val device: BluetoothDevice = obtenerDispositivoBluetooth() { lambda }

        // Intentar conectar al servidor Bluetooth
        bluetoothClient.connectToServer(device)
    }

    private fun enviarEmail() {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (binding.soyConcent.isChecked)
            bluetoothServer.onRequestPermissionsResult(requestCode, permissions, grantResults)
        else
            bluetoothClient.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetoothServer.onActivityResult(requestCode, resultCode, data)
    }

    private fun obtenerDispositivoBluetooth(activity: Activity, adapter: BluetoothAdapter, callback: (BluetoothDevice?) -> Unit) {
        val bluetoothDeviceSelector = BluetoothDeviceSelector(activity, adapter)
        bluetoothDeviceSelector.startDiscovery { device ->
            callback(device)
        }
    }
}