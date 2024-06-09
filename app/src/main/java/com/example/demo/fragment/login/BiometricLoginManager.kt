package com.example.demo.fragment.login

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.example.demo.R

class BiometricLoginManager(private val context: Context) {

    private lateinit var biometricPrompt: BiometricPrompt

    fun authenticate(callback: BiometricAuthenticationCallback) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.bio_titulo))
            .setSubtitle(context.getString(R.string.bio_subtitulo))
            .setNegativeButtonText(context.getString(R.string.varias_cancelar))
            .build()

        biometricPrompt = BiometricPrompt(
            context as AppCompatActivity,
            context.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onAuthenticationError(errorCode, errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onAuthenticationSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onAuthenticationFailed()
                }
            })
        val uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, throwable ->
            // Manejar la excepción aquí
            Log.e("UncaughtException", "Excepción no manejada en hilo secundario: $throwable")
        }

        // Crea e inicia un nuevo hilo con el manejador de excepciones
        val thread = Thread {
            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                // Manejar la excepción aquí
                Log.e("Exception", "Excepción capturada en el hilo secundario: $e")
            }
        }
        thread.uncaughtExceptionHandler = uncaughtExceptionHandler
        thread.start()
    }

    interface BiometricAuthenticationCallback {
        fun onAuthenticationSuccess()
        fun onAuthenticationError(errorCode: Int, errorMessage: String)
        fun onAuthenticationFailed()
    }
}