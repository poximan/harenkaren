package com.example.demo.fragment.maps

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.demo.R
import com.example.demo.model.UnidSocial
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class BarCharInfoWindow(
    private val context: Context,
    private val unSoc: UnidSocial,
    layoutResId: Int,
    mapView: MapView
) : InfoWindow(layoutResId, mapView) {

    private var initialX = 0f
    private var initialY = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    override fun onOpen(item: Any?) {
        // Inflar la vista del layout personalizado
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_osm_bubble, null)

        val linearLayout = view.findViewById<LinearLayout>(R.id.osm_bubble)
        val btnCerrar = view.findViewById<ImageButton>(R.id.close_button)
        btnCerrar.setOnClickListener { close() }

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = v.x - event.rawX
                    initialY = v.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    offsetX = event.rawX + initialX
                    offsetY = event.rawY + initialY
                    v.animate()
                        .x(offsetX)
                        .y(offsetY)
                        .setDuration(0)
                        .start()
                }
            }
            true
        }

        val contadoresNoNulos = unSoc.getContadoresNoNulos()
        for (atribString in contadoresNoNulos) {

            val valorAtributo = unSoc.javaClass.getDeclaredField(atribString)
            valorAtributo.isAccessible = true
            // utilizar el objeto Field para obtener el valor del atributo en unidSocial.
            val valor = valorAtributo.get(unSoc) as Int

            val barra = apilarBarra(atribString, valor)
            linearLayout.addView(barra)
        }

        mView = view
    }

    override fun onClose() {
        // Realizar acciones cuando se cierra la ventana de información del marcador
    }

    private fun apilarBarra(atribString: String, valor: Int): LinearLayout {

        // Crear un nuevo LinearLayout para contener el View y el TextView
        val nestedLinearLayout = LinearLayout(context)
        nestedLinearLayout.orientation = LinearLayout.HORIZONTAL
        val nestedLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nestedLinearLayout.layoutParams = nestedLayoutParams

        // Crear y configurar el cuadrado azul
        val squareView = View(context)
        squareView.setBackgroundColor(siguienteColor(atribString))
        val squareLayoutParams = LinearLayout.LayoutParams(20, valor * 2)
        squareLayoutParams.gravity = Gravity.CENTER_VERTICAL // Centrar verticalmente
        squareView.layoutParams = squareLayoutParams

        // Crear y configurar el TextView
        val textView = TextView(context)
        textView.text = "$atribString($valor)"
        textView.textSize = when {
            valor < 10f -> 10f
            valor > 14f -> 14f
            else -> valor.toFloat()
        }
        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textLayoutParams.gravity = Gravity.CENTER_VERTICAL // Centrar verticalmente
        textLayoutParams.leftMargin = 8
        textView.layoutParams = textLayoutParams

        // Agregar el View y el TextView al nuevo LinearLayout
        nestedLinearLayout.addView(squareView)
        nestedLinearLayout.addView(textView)
        return nestedLinearLayout
    }

    private fun siguienteColor(atribString: String): Int {

        val coloresMap = mapOf(
            "vAlfaS4Ad" to R.color.clr_v_alfa_s4ad,
            "vAlfaSams" to R.color.clr_v_alfa_sams,
            "vHembrasAd" to R.color.clr_v_hembras_ad,
            "vCrias" to R.color.clr_v_crias,
            "vDestetados" to R.color.clr_v_destetados,
            "vJuveniles" to R.color.clr_v_juveniles,
            "vS4AdPerif" to R.color.clr_v_s4ad_perif,
            "vS4AdCerca" to R.color.clr_v_s4ad_cerca,
            "vS4AdLejos" to R.color.clr_v_s4ad_lejos,
            "vOtrosSamsPerif" to R.color.clr_v_otros_sams_perif,
            "vOtrosSamsCerca" to R.color.clr_v_otros_sams_cerca,
            "vOtrosSamsLejos" to R.color.clr_v_otros_sams_lejos,
            "mAlfaS4Ad" to R.color.clr_m_alfa_s4ad,
            "mAlfaSams" to R.color.clr_m_alfa_sams,
            "mHembrasAd" to R.color.clr_m_hembras_ad,
            "mCrias" to R.color.clr_m_crias,
            "mDestetados" to R.color.clr_m_destetados,
            "mJuveniles" to R.color.clr_m_juveniles,
            "mS4AdPerif" to R.color.clr_m_s4ad_perif,
            "mS4AdCerca" to R.color.clr_m_s4ad_cerca,
            "mS4AdLejos" to R.color.clr_m_s4ad_lejos,
            "mOtrosSamsPerif" to R.color.clr_m_otros_sams_perif,
            "mOtrosSamsCerca" to R.color.clr_m_otros_sams_cerca,
            "mOtrosSamsLejos" to R.color.clr_m_otros_sams_lejos
        )

        return ContextCompat.getColor(context, coloresMap[atribString]!!)
    }
}

