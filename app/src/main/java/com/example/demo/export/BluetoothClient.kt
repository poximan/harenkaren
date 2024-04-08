import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class BluetoothClient(private val activity: Activity, private val adapter: BluetoothAdapter) {

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 3
        private val BLUETOOTH_UUID = UUID.fromString("UUID_HERE")
    }

    fun connectToServer(device: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes el permiso BLUETOOTH, solicítalo
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            // Si ya tienes el permiso BLUETOOTH, intenta conectar al servidor Bluetooth
            connect(device)
        }
    }

    private fun connect(device: BluetoothDevice) {
        try {
            val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID)
            socket.connect()
            // Aquí puedes leer y escribir datos a través del socket Bluetooth
            // Ejemplo: socket.inputStream.read(buffer)
            // Ejemplo: socket.outputStream.write(data)
        } catch (e: IOException) {
            // Manejar error de conexión
        } catch (e: SecurityException) {
            // Manejar el caso en que no tienes permiso para acceder al Bluetooth
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Permiso otorgado, intenta conectar al servidor Bluetooth
            // Aquí necesitarías tener una referencia al dispositivo Bluetooth al que deseas conectarte
            val device = adapter.getRemoteDevice("MAC_Address_of_the_Device")
            connect(device)
        } else {
            // Permiso denegado, manejar la situación adecuadamente
        }
    }
}