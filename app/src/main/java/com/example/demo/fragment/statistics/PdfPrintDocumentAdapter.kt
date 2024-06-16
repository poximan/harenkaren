package com.example.demo.fragment.statistics

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
) : PrintDocumentAdapter() {

    private var pdfDocument: PdfDocument? = null
    private var pageWidth: Int = 0
    private var pageHeight: Int = 0

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)
        /*
        widthMils/1000: Convierte el tamaño del papel de milésimas de pulgada (como lo usa PrintAttributes) a pulgadas
        se multiplica por 72 para convierte el tamaño de pulgadas a puntos. En la tipografía, un punto (pt) es 1/72 de una pulgada.
         */
        val widthMils = newAttributes.mediaSize!!.widthMils / 1000 * 72
        val heightMils = newAttributes.mediaSize!!.heightMils / 1000 * 72
        val pageInfo = PdfDocument.PageInfo.Builder(widthMils, heightMils, 1).create()

        pageWidth = pageInfo.pageWidth
        pageHeight = pageInfo.pageHeight

        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("Reporte.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal,
        callback: WriteResultCallback
    ) {
        val bitmap = getBitmapFromView(scrollView, pageWidth)

        val totalPages = (bitmap.height / pageHeight.toFloat()).toInt() + 1
        for (i in 0 until totalPages) {
            if (cancellationSignal.isCanceled) {
                callback.onWriteCancelled()
                pdfDocument!!.close()
                pdfDocument = null
                return
            }

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create()
            val page = pdfDocument!!.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint()
            val srcRect = android.graphics.Rect(0, i * pageHeight, pageWidth, (i + 1) * pageHeight)
            val destRect = android.graphics.Rect(0, 0, pageWidth, pageHeight)

            canvas.drawBitmap(bitmap, srcRect, destRect, paint)
            pdfDocument!!.finishPage(page)
        }

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
        view.layout(0, 0, pageWidth, view.measuredHeight)

        val bitmap =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}