package phocidae.mirounga.leonina.servicios

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.exception.IndiceNoExisteExcepcion
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.model.UnidSocial
import java.util.Locale

class IdiomaAdapter {

    /*
    recorrido
     */
    fun persistenciaRecorr(context: Context, recorrido: Recorrido): Recorrido {
        // Guarda el idioma actual
        val originalConfig = context.resources.configuration
        val originalLocale = Locale.getDefault()

        // Obtiene el índice del valor de marea en el idioma actual
        val mareaIndex = getAtribIndex(context, recorrido.marea, R.array.op_marea)
        if (mareaIndex == -1)
            throw IndiceNoExisteExcepcion()

        // Establece el idioma por defecto de la app
        setAppLocale(context, "es_ES")
        val defaultMarea = getAtribValue(context, mareaIndex, R.array.op_marea)
        restoreAppLocale(context, originalConfig, originalLocale)

        return recorrido.copy(marea = defaultMarea)
    }

    fun viewModelRecorr(context: Context, recorrido: Recorrido): Recorrido {
        // Guarda el idioma actual
        val originalConfig = context.resources.configuration
        val originalLocale = Locale.getDefault()

        // Establece el idioma por defecto de la app
        setAppLocale(context, "es_ES")

        // Obtiene el índice del valor de marea en el idioma por defecto
        val mareaIndex = getAtribIndex(context, recorrido.marea, R.array.op_marea)
        restoreAppLocale(context, originalConfig, originalLocale)

        // Si no se encuentra el índice, devuelve el recorrido sin cambios
        if (mareaIndex == -1)
            throw IndiceNoExisteExcepcion()

        val originalMarea = getAtribValue(context, mareaIndex, R.array.op_marea)
        return recorrido.copy(marea = originalMarea)
    }

    /*
    unidad social
     */
    fun persistenciaUnSoc(context: Context, unidSocial: UnidSocial): UnidSocial {
        // Guarda el idioma actual
        val originalConfig = context.resources.configuration
        val originalLocale = Locale.getDefault()

        // Obtiene el índice del valor de marea en el idioma actual
        val ptoObsIndex = getAtribIndex(context, unidSocial.ptoObsUnSoc, R.array.op_punto_obs_unsoc)
        val ctxSocIndex = getAtribIndex(context, unidSocial.ctxSocial, R.array.op_contexto_social)
        val tpoSustIndex = getAtribIndex(context, unidSocial.tpoSustrato, R.array.op_tipo_sustrato)

        if (ptoObsIndex == -1 || ctxSocIndex == -1 || tpoSustIndex == -1)
            throw IndiceNoExisteExcepcion()

        // Establece el idioma por defecto de la app
        setAppLocale(context, "es_ES")
        val defPtoObs = getAtribValue(context, ptoObsIndex, R.array.op_punto_obs_unsoc)
        val defCtxSoc = getAtribValue(context, ctxSocIndex, R.array.op_contexto_social)
        val defTpoSust = getAtribValue(context, tpoSustIndex, R.array.op_tipo_sustrato)
        restoreAppLocale(context, originalConfig, originalLocale)

        return unidSocial.copy(
            ptoObsUnSoc = defPtoObs, ctxSocial = defCtxSoc, tpoSustrato = defTpoSust
        )
    }

    fun viewModelUnSoc(context: Context, unidSocial: UnidSocial): UnidSocial {
        // Guarda el idioma actual
        val originalConfig = context.resources.configuration
        val originalLocale = Locale.getDefault()

        // Establece el idioma por defecto de la app
        setAppLocale(context, "es_ES")

        // Obtiene el índice del valor de marea en el idioma por defecto
        val ptoObsIndex = getAtribIndex(context, unidSocial.ptoObsUnSoc, R.array.op_punto_obs_unsoc)
        val ctxSocIndex = getAtribIndex(context, unidSocial.ctxSocial, R.array.op_contexto_social)
        val tpoSustIndex = getAtribIndex(context, unidSocial.tpoSustrato, R.array.op_tipo_sustrato)
        restoreAppLocale(context, originalConfig, originalLocale)

        if (ptoObsIndex == -1 || ctxSocIndex == -1 || tpoSustIndex == -1)
            throw IndiceNoExisteExcepcion()

        val defPtoObs = getAtribValue(context, ptoObsIndex, R.array.op_punto_obs_unsoc)
        val defCtxSoc = getAtribValue(context, ctxSocIndex, R.array.op_contexto_social)
        val defTpoSust = getAtribValue(context, tpoSustIndex, R.array.op_tipo_sustrato)
        return unidSocial.copy(
            ptoObsUnSoc = defPtoObs, ctxSocial = defCtxSoc, tpoSustrato = defTpoSust
        )
    }

    /*
    auxiliares
     */
    // Función para establecer el idioma de la aplicación
    private fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val resources: Resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // Función para restaurar el idioma original de la aplicación
    private fun restoreAppLocale(
        context: Context,
        originalConfig: Configuration,
        originalLocale: Locale
    ) {
        Locale.setDefault(originalLocale)
        val config = Configuration(originalConfig)
        config.setLocale(originalLocale)

        val resources: Resources = context.resources
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getAtribIndex(context: Context, atrib: String, recursoId: Int): Int {
        val atribLista = context.resources.getStringArray(recursoId)
        return atribLista.indexOf(atrib)
    }

    private fun getAtribValue(context: Context, index: Int, recursoId: Int): String {
        val atribLista = context.resources.getStringArray(recursoId)
        return atribLista.getOrNull(index) ?: ""
    }
}
