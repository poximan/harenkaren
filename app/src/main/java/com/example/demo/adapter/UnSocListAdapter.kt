package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.UnidSocial
import java.io.File

class UnSocListAdapter(
    private val itemClickListener: OnUnSocClickListener
) : RecyclerView.Adapter<UnSocListAdapter.UnSocViewHolder>() {

    private var unidSocialList = emptyList<UnidSocial>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnSocViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_unsoc, parent, false)
        return UnSocViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnSocViewHolder, position: Int) {
        val unSoc = unidSocialList[position]
        holder.bind(unSoc)
    }

    internal fun setUnSoc(unidSocialList: List<UnidSocial>) {
        this.unidSocialList = unidSocialList
        notifyDataSetChanged()
    }

    inner class UnSocViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val id: TextView = view.findViewById(R.id.num_unsoc)
        private val unSocResumen: TextView = view.findViewById(R.id.unsoc_resumen)
        private val date: TextView = view.findViewById(R.id.unsoc_timestamp)

        fun bind(unidSocial: UnidSocial) {
            itemView.setOnClickListener { itemClickListener.onItemClick(unidSocial) }

            id.text = unidSocial.id.toString()
            unSocResumen.text =
                        " vivos --> *AlfaS4/Ad: " + unidSocial.vAlfaS4Ad + " *AlfaOt.SA: " + unidSocial.vAlfaOtrosSA +
                        " *Hem.Ad.: " + unidSocial.vHembrasAd + " *Crias: " + unidSocial.vCrias + " *Dest.: " + unidSocial.vDestetados +
                        " *Juv: " + unidSocial.vJuveniles + " *S4/Ad Perif.: " + unidSocial.vS4AdPerif + " *S4/Ad Cerca: " + unidSocial.vS4AdCerca +
                        " *S4/Ad Lejos: " + unidSocial.vS4AdLejos + " *Ot.SA Perif.: " + unidSocial.vOtrosSAPerif +
                        " *Ot.SA Cerca: " + unidSocial.vOtrosSACerca + " *Ot.SA Lejos: " + unidSocial.vOtrosSALejos +
                        "\n" +
                        " muertos --> *AlfaS4/Ad: " + unidSocial.mAlfaS4Ad + " *AlfaOt.SA: " + unidSocial.mAlfaOtrosSA +
                        " *Hem.Ad.: " + unidSocial.mHembrasAd + " *Crias: " + unidSocial.mCrias + " *Dest.: " + unidSocial.mDestetados +
                        " *Juv.: " + unidSocial.mJuveniles + " *S4/Ad Perif.: " + unidSocial.mS4AdPerif + " *S4/Ad Cerca: " + unidSocial.mS4AdCerca +
                        " *S4/Ad Lejos: " + unidSocial.mS4AdLejos + " *Ot.SA Perif.: " + unidSocial.mOtrosSAPerif +
                        " *Ot.SA Cerca: " + unidSocial.mOtrosSACerca + " *Ot.SA Lejos: " + unidSocial.mOtrosSALejos +
                        "\n\n" + " *Comentario: " + unidSocial.comentario

            date.text = unidSocial.date


            val file = File(unidSocial.photoPath)
            /*
            if (file.exists()) {

                val imageBitmap: Bitmap = BitmapFactory.decodeFile(unidSocial.photoPath)
                val exif = ExifInterface(unidSocial.photoPath.toString())
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
             */
        }
    }

    override fun getItemCount() = unidSocialList.size

    interface OnUnSocClickListener {
        fun onItemClick(elem: UnidSocial)
    }
}