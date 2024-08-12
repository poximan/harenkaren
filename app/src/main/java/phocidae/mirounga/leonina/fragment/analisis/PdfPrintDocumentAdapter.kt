package phocidae.mirounga.leonina.fragment.analisis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.view.View
import java.io.FileOutputStream
import java.io.IOException

class PdfPrintDocumentAdapter(
    private val context: Context,
    private val scrollView: View,
    rangoFechas: String
) : PrintDocumentAdapter() {

    private var pdfDocument: PdfDocument? = null
    private val pageWidth: Int = 595  // Ancho A4 en puntos (210 mm x 72 dpi)
    private var pageHeight: Int = 0
    private val rangoFechas = rangoFechas

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle
    ) {
        // Calcular la altura de la página basada en el contenido
        pageHeight = getPageHeight()

        // Verificar que el ancho y la altura sean válidos
        if (pageWidth <= 0 || pageHeight <= 0) {
            callback.onLayoutCancelled()
            return
        }

        pdfDocument = PrintedPdfDocument(context, newAttributes)

        val info = PrintDocumentInfo.Builder("Reporte ${rangoFechas}.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal,
        callback: WriteResultCallback
    ) {
        val bitmap = getBitmapFromView(scrollView, pageWidth)

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument!!.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        val srcRect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        val destRect = android.graphics.Rect(0, 0, pageWidth, pageHeight)

        canvas.drawBitmap(bitmap, srcRect, destRect, paint)
        pdfDocument!!.finishPage(page)

        try {
            pdfDocument!!.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument!!.close()
            pdfDocument = null
        }

        callback.onWriteFinished(pageRanges)
    }

    private fun getBitmapFromView(view: View, pageWidth: Int): Bitmap {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getPageHeight(): Int {
        // Ajusta la altura de la página según el contenido
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        scrollView.measure(pageWidth, heightSpec)
        scrollView.layout(0, 0, scrollView.measuredWidth, scrollView.measuredHeight)
        return scrollView.measuredHeight
    }
}