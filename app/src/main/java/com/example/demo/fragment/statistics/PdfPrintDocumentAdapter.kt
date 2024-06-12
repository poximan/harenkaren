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

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)

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
        val bitmap = getBitmapFromView(scrollView)

        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument!!.startPage(pageInfo)

        if (cancellationSignal.isCanceled) {
            callback.onWriteCancelled()
            pdfDocument!!.close()
            pdfDocument = null
            return
        }

        val canvas = page.canvas
        val paint = Paint()
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
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

    private fun getBitmapFromView(view: View): Bitmap {
        val height = view.height
        val bitmap = Bitmap.createBitmap(view.width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}