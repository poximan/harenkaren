package com.example.demo

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView

class FunkyFlotante(private val activity: Activity) {

    private var popupWindow: PopupWindow? = null

    fun funkeala(message: String) {
        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.funky_toast_layout, null)

        val textView = view.findViewById<TextView>(R.id.funky_toast)
        textView.text = message

        popupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX.toInt()
                    val newY = event.rawY.toInt()
                    popupWindow?.update(newX, newY, -1, -1)
                }
            }
            true
        }
        popupWindow?.showAtLocation(activity.window.decorView, Gravity.NO_GRAVITY, 0, 0)
    }

    private fun noFunk() {
        popupWindow?.dismiss()
        popupWindow = null
    }
}
