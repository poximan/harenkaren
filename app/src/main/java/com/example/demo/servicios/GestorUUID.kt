package com.example.demo.servicios

import android.provider.Settings
import com.example.demo.activity.MainActivity
import java.nio.charset.StandardCharsets
import java.util.UUID

class GestorUUID {
    companion object {

        private val resolveitor = MainActivity.Companion.resolver

        fun obtenerAndroidID(): String {
            val androidID = Settings.Secure.getString(resolveitor, Settings.Secure.ANDROID_ID)
            return androidID + "@" + android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL
        }

        fun obtenerUUID(cadena: String): UUID {
            val tt = System.currentTimeMillis()
            return UUID.nameUUIDFromBytes("$cadena:$tt".toByteArray(StandardCharsets.UTF_8))
        }
    }
}