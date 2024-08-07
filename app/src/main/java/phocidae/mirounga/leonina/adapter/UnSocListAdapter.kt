package phocidae.mirounga.leonina.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.model.UnidSocial

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

            id.text = unidSocial.orden.toString()
            unSocResumen.text = armarResumen(unidSocial)
            date.text = unidSocial.date
        }
    }

    fun armarResumen(unidSocial: UnidSocial): String {
        val context = (itemClickListener as Fragment).requireContext()

        return buildString {
            append(context.getString(R.string.soc_listAdap_gral))

            if (unidSocial.ptoObsUnSoc.isNotEmpty()) append("*Pto.obs.: ${unidSocial.ptoObsUnSoc} ")
            if (unidSocial.ctxSocial.isNotEmpty()) append("*Ctx.soc: ${unidSocial.ctxSocial} ")
            if (unidSocial.tpoSustrato.isNotEmpty()) append("*Pya: ${unidSocial.tpoSustrato} ")
            append("\n")

            append(context.getString(R.string.soc_listAdap_vivos))
            if (unidSocial.vAlfaS4Ad > 0) append("*AlfaS4/Ad: ${unidSocial.vAlfaS4Ad} ")
            if (unidSocial.vAlfaSams > 0) append("*AlfaOt.Sams: ${unidSocial.vAlfaSams} ")
            if (unidSocial.vHembrasAd > 0) append("*Hem.Ad.: ${unidSocial.vHembrasAd} ")
            if (unidSocial.vCrias > 0) append("*Crias: ${unidSocial.vCrias} ")
            if (unidSocial.vDestetados > 0) append("*Dest.: ${unidSocial.vDestetados} ")
            if (unidSocial.vJuveniles > 0) append("*Juv: ${unidSocial.vJuveniles} ")
            if (unidSocial.vS4AdPerif > 0) append("*S4/Ad Perif.: ${unidSocial.vS4AdPerif} ")
            if (unidSocial.vS4AdCerca > 0) append("*S4/Ad Cerca: ${unidSocial.vS4AdCerca} ")
            if (unidSocial.vS4AdLejos > 0) append("*S4/Ad Lejos: ${unidSocial.vS4AdLejos} ")
            if (unidSocial.vOtrosSamsPerif > 0) append("*Ot.Sams Perif.: ${unidSocial.vOtrosSamsPerif} ")
            if (unidSocial.vOtrosSamsCerca > 0) append("*Ot.Sams Cerca: ${unidSocial.vOtrosSamsCerca} ")
            if (unidSocial.vOtrosSamsLejos > 0) append("*Ot.Sams Lejos: ${unidSocial.vOtrosSamsLejos} ")
            if (endsWith(context.getString(R.string.soc_listAdap_vivos))) {
                delete(length - context.getString(R.string.soc_listAdap_vivos).length, length)
            } else append("\n")

            append(context.getString(R.string.soc_listAdap_muertos))
            if (unidSocial.mAlfaS4Ad > 0) append("*AlfaS4/Ad: ${unidSocial.mAlfaS4Ad} ")
            if (unidSocial.mAlfaSams > 0) append("*AlfaOt.Sams: ${unidSocial.mAlfaSams} ")
            if (unidSocial.mHembrasAd > 0) append("*Hem.Ad.: ${unidSocial.mHembrasAd} ")
            if (unidSocial.mCrias > 0) append("*Crias: ${unidSocial.mCrias} ")
            if (unidSocial.mDestetados > 0) append("*Dest.: ${unidSocial.mDestetados} ")
            if (unidSocial.mJuveniles > 0) append("*Juv.: ${unidSocial.mJuveniles} ")
            if (unidSocial.mS4AdPerif > 0) append("*S4/Ad Perif.: ${unidSocial.mS4AdPerif} ")
            if (unidSocial.mS4AdCerca > 0) append("*S4/Ad Cerca: ${unidSocial.mS4AdCerca} ")
            if (unidSocial.mS4AdLejos > 0) append("*S4/Ad Lejos: ${unidSocial.mS4AdLejos} ")
            if (unidSocial.mOtrosSamsPerif > 0) append("*Ot.Sams Perif.: ${unidSocial.mOtrosSamsPerif} ")
            if (unidSocial.mOtrosSamsCerca > 0) append("*Ot.Sams Cerca: ${unidSocial.mOtrosSamsCerca} ")
            if (unidSocial.mOtrosSamsLejos > 0) append("*Ot.Sams Lejos: ${unidSocial.mOtrosSamsLejos} ")
            if (endsWith(context.getString(R.string.soc_listAdap_muertos))) {
                delete(length - context.getString(R.string.soc_listAdap_muertos).length, length)
            } else append("\n")

            if (unidSocial.comentario?.isNotEmpty() == true) append("*Comentario: ${unidSocial.comentario}")
            if (endsWith("\n")) {
                delete(length - "\n".length, length)
            }
        }
    }

    override fun getItemCount() = unidSocialList.size

    interface OnUnSocClickListener {
        fun onItemClick(elem: UnidSocial)
    }
}