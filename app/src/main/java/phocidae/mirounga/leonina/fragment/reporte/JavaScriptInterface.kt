package phocidae.mirounga.leonina.fragment.reporte

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.JavascriptInterface

class JavaScriptInterface(
    private val listener: OnImageCapturedListener,
    private val activity: Activity
) {
    /*
    esto lo ejecuta el cliente webview, por eso figura gris la referencia.
    ver webViewHeat.addJavascriptInterface(JavaScriptInterface(this....) en ReportesFragment
     */
    @JavascriptInterface
    fun onImageCaptured(dataUrl: String) {
        val base64Image = dataUrl.split(",")[1]
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        activity.runOnUiThread {
            listener.onImageCaptured(bitmap)
        }
    }
}
