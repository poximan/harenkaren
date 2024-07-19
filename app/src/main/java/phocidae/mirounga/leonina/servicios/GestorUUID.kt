package phocidae.mirounga.leonina.servicios

import android.provider.Settings
import phocidae.mirounga.leonina.activity.MainActivity
import phocidae.mirounga.leonina.exception.UUIDRepetidoExcepcion
import java.util.UUID

class GestorUUID {
    companion object {

        private val resolveitor = MainActivity.resolver
        private var actual: UUID = UUID.randomUUID()
        private var ultimo: UUID = UUID.randomUUID()

        fun obtenerAndroidID(): String {
            val androidID = Settings.Secure.getString(resolveitor, Settings.Secure.ANDROID_ID)
            return androidID + "@" + android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL
        }

        fun obtenerUUID(): UUID {
            actual = UUID.randomUUID()
            if (actual != ultimo)
                ultimo = actual
            else
                throw UUIDRepetidoExcepcion()
            return ultimo
        }
    }
}