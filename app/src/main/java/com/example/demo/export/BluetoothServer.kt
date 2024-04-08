import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class BluetoothServer(private val activity: Activity, private val adapter: BluetoothAdapter) {

    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1
        private const val REQUEST_BLUETOOTH_PERMISSION = 2
        private val BLUETOOTH_UUID = UUID.fromString("UUID_HERE")
    }

    private lateinit var serverSocket: BluetoothServerSocket

    fun startServer() {
        if (adapter.isEnabled) {
            checkBluetoothPermissions()
        } else {
            enableBluetooth()
        }
    }

    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            createServerSocket()
        }
    }

    private fun createServerSocket() {
        try {
            serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothServer", BLUETOOTH_UUID)
            var socket: BluetoothSocket? = null
            var continueAccepting = true
            while (continueAccepting) {
                try {
                    socket = serverSocket.accept()
                } catch (e: IOException) {
                    continueAccepting = false
                    break
                }
                socket?.let {
                    // Aquí puedes leer y escribir datos a través del socket Bluetooth
                    // Ejemplo: socket.inputStream.read(buffer)
                    // Ejemplo: socket.outputStream.write(data)
                    it.close()
                    continueAccepting = false
                }
            }
        } catch (e: IOException) {
            // Manejar error al crear el socket del servidor
        } catch (e: SecurityException) {
            // Manejar el caso en que no tienes permiso para acceder al Bluetooth
        }
    }

    private fun enableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                REQUEST_ENABLE_BLUETOOTH
            )
        } else {
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            createServerSocket()
        } else {
            // Permiso denegado, manejar la situación adecuadamente
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_OK) {
            checkBluetoothPermissions()
        } else {
            // El usuario no habilitó el Bluetooth, manejar la situación adecuadamente
        }
    }

    fun cancel() {
        try {
            serverSocket.close()
        } catch (e: IOException) {
            // Manejar error de cierre del socket
        }
    }
}
