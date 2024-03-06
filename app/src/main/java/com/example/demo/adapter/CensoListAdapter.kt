package com.example.demo.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.Censo
import java.io.File

class CensoListAdapter(
    private val itemClickListener: OnCensoClickListener
) : RecyclerView.Adapter<CensoListAdapter.ReportViewHolder>() {

    private var censos = emptyList<Censo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_censo, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = censos[position]
        holder.bind(report)
    }

    internal fun setCensos(censos: List<Censo>) {
        this.censos = censos
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reporte_resumen: TextView = view.findViewById(R.id.report_resumen)
        private val date: TextView = view.findViewById(R.id.report_timestamp)
        private val image: ImageView = view.findViewById(R.id.report_imageView)

        fun bind(censo: Censo) {
            itemView.setOnClickListener { itemClickListener.onItemClick(censo) }
            reporte_resumen.text = "Pto.obs.: " + censo.ptoObsCenso + " - Ctx.social: " + censo.ctxSocial + "Tpo.sust.: " + censo.tpoSustrato +
                    " / AlfaS4/Ad: " + censo.alfaS4Ad + " - OtrosSA: " + censo.alfaOtrosSA
            date.text = censo.date

            val file = File(censo.photoPath)
            if (file.exists()) {

                val imageBitmap: Bitmap = BitmapFactory.decodeFile(censo.photoPath)
                val exif = ExifInterface(censo.photoPath.toString())
                val orientation: Int =
                    exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                Log.i("orientation", orientation.toString())

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        matrix.setRotate(90F)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        matrix.setRotate(180F)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        matrix.setRotate(270F)
                    }
                }

                val rotatedBitmap =
                    Bitmap.createBitmap(
                        imageBitmap,
                        0,
                        0,
                        imageBitmap.width,
                        imageBitmap.height,
                        matrix,
                        true
                    )

                image.setImageBitmap(rotatedBitmap)
            }
        }
    }

    override fun getItemCount() = censos.size

    interface OnCensoClickListener {
        fun onItemClick(censo: Censo)
    }
}