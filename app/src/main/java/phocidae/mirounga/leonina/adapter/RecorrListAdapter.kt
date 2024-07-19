package phocidae.mirounga.leonina.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.model.Recorrido

class RecorrListAdapter(
    private val itemListener: OnRecorrClickListener
) : RecyclerView.Adapter<RecorrListAdapter.RecorrViewHolder>() {

    private var recorridoList = emptyList<Recorrido>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecorrViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recorrido, parent, false)
        return RecorrViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecorrViewHolder, position: Int) {
        val recorr = recorridoList[position]
        holder.bind(recorr)
    }

    internal fun setRecorrido(recorridoList: List<Recorrido>) {
        this.recorridoList = recorridoList
        notifyDataSetChanged()
    }

    inner class RecorrViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val id: TextView = view.findViewById(R.id.num_recorr)
        private val resumen: TextView = view.findViewById(R.id.text_resumen)
        private val icono: ImageView = view.findViewById(R.id.grafRecorr)

        init {
            view.setOnClickListener {
                itemListener.onItemClick(recorridoList[adapterPosition])
            }
            icono.setOnClickListener {
                itemListener.onIconClick(recorridoList[adapterPosition])
            }
        }

        fun bind(recorrido: Recorrido) {
            val context = (itemListener as Fragment).requireContext()

            id.text = recorrido.orden.toString()
            resumen.text = """
                ${context.getString(R.string.rec_observador)}: ${recorrido.observador}
                ${context.getString(R.string.varias_area)}: ${recorrido.areaRecorrida}
                ${context.getString(R.string.rec_meteo)}: ${recorrido.meteo}
                ${context.getString(R.string.rec_marea)}: ${recorrido.marea}
                ${context.getString(R.string.rec_observaciones)}: ${recorrido.observaciones}
            """.trimIndent()
        }
    }

    override fun getItemCount() = recorridoList.size

    interface OnRecorrClickListener {
        fun onItemClick(elem: Recorrido)
        fun onIconClick(elem: Recorrido)
    }
}